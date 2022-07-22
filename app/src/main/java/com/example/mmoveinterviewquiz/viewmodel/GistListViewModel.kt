package com.example.mmoveinterviewquiz.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.mmoveinterviewquiz.repository.github.GithubRepositoryImpl
import com.example.mmoveinterviewquiz.repository.model.ErrorCode
import com.example.mmoveinterviewquiz.repository.model.Gist
import com.example.mmoveinterviewquiz.repository.model.RepositoryResult
import com.example.mmoveinterviewquiz.view.common.FormatWrap
import com.example.mmoveinterviewquiz.view.common.StringWrap
import com.example.mmoveinterviewquiz.view.common.TextWrap
import com.example.mmoveinterviewquiz.viewmodel.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GistListViewModel @Inject constructor(private val repository: GithubRepositoryImpl): BaseViewModel() {

    companion object {
        private const val USER_GISTS_THRESHOLD = 5
        private const val BATCH_LOADING_SIZE = 4
    }

    private val _gistListUIModel= MutableStateFlow(listOf<GistListUIItem>())
    private val _showLoadingSpinner = MutableStateFlow(false)
    private val _snackbarMessage = MutableSharedFlow<TextWrap>(replay = 0)

    val gistListUIModel:StateFlow<List<GistListUIItem>> = _gistListUIModel
    val showLoadingSpinner: StateFlow<Boolean> = _showLoadingSpinner
    val snackbarMessage: Flow<TextWrap> = _snackbarMessage


    fun onClickFavoriteGist(gistId: String) {
        launchLoadingScope {
            val clickedItem = (_gistListUIModel.value.firstOrNull{it.id == gistId} as? GistListUIItem.Gist) ?: return@launchLoadingScope
            val res = when (clickedItem.isFavorite) {
                true -> repository.deleteFavoriteAsync(viewModelScope, clickedItem.id)
                false -> repository.addFavoriteAsync(viewModelScope, clickedItem.id)
            }.await()

            when (res) {
                is RepositoryResult.Success -> {
                    val newList = _gistListUIModel.value.mapIndexed { idx, item ->
                        when (item.id == gistId && item is GistListUIItem.Gist) {
                            true -> item.copy(
                                isFavorite = !item.isFavorite
                            )
                            false -> item
                        }
                    }
                    _gistListUIModel.value = newList
                }
                is RepositoryResult.Fail -> {
                    onResponseFail(res)
                }
            }
        }

    }

    fun onClickItem(gistId: String) {

    }

    fun initViewModel() {
        launchLoadingScope {
            val fetchGistsReq = repository.fetchGistsAsync(viewModelScope)
            val fetchFavoritesReq = repository.fetchFavoritesAsync(viewModelScope)
            val fetchGistRes = fetchGistsReq.await()
            val fetchFavRes = fetchFavoritesReq.await()

            when {
                fetchGistRes is RepositoryResult.Success && fetchFavRes is RepositoryResult.Success -> {
                    val favList = fetchFavRes.data
                    val gistsUIList = fetchGistRes.data.map {
                        GistListUIItem.Gist(
                            id = it.id,
                            username = it.username,
                            idText = StringWrap(it.id),
                            url = StringWrap(it.url),
                            isFavorite = it.id in favList,
                            csvFileName = StringWrap("TODO"),
                        )
                    }
                    _gistListUIModel.value = gistsUIList
                }
                fetchGistRes is RepositoryResult.Fail -> {
                    onResponseFail(fetchGistRes)
                }
                fetchFavRes is RepositoryResult.Fail -> {
                    onResponseFail(fetchFavRes)
                }
            }
            startFetchingUsersGists()
        }
    }

    private fun startFetchingUsersGists() {
        launchLoadingScope {
            val usernames = _gistListUIModel.value.filterIsInstance<GistListUIItem.Gist>().map {
                it.username
            }
            usernames.distinct().chunked(BATCH_LOADING_SIZE).forEach { res ->
                val userGistsResultList = res.map {
                    repository.fetchUserGistsAsync(viewModelScope, it)
                }.awaitAll()

                when(userGistsResultList.all { it is RepositoryResult.Success<List<Gist>> }) {
                    true -> {
                        userGistsResultList as List<RepositoryResult.Success<List<Gist>>>
                        val userGistsCountMap = userGistsResultList.map {
                            it.data.first().username to it.data.size
                        }.toMap()

                        val currentGistUIList = _gistListUIModel.value
                        val newGistUIList = mutableListOf<GistListUIItem>()
                        currentGistUIList.forEach {
                            newGistUIList.add(it)
                            val gistsCount = userGistsCountMap.getOrDefault(it.username, -1)
                            if (it is GistListUIItem.Gist && gistsCount > USER_GISTS_THRESHOLD) {
                                newGistUIList.add(
                                    GistListUIItem.UserInfo(
                                        id = it.id,
                                        username = it.username,
                                        info = FormatWrap(
                                            StringWrap("This user is %s.\nHe/She has gists count of %s."),
                                            StringWrap(it.username),
                                            StringWrap(gistsCount.toString()))
                                    )
                                )
                            }
                        }
                        _gistListUIModel.value = newGistUIList
                    }
                    false -> {
                        val failResult = userGistsResultList.filterIsInstance<RepositoryResult.Fail>().first()
                        onResponseFail(failResult)
                    }
                }
            }
        }
    }


    private fun onResponseFail(error: RepositoryResult.Fail) {
        viewModelScope.launch(Dispatchers.Main) {
            _snackbarMessage.emit(
                when (error.errorCode) {
                    ErrorCode.HTTPError -> {
                        FormatWrap(StringWrap("API return error (%s)"), StringWrap(error.errorMessage?.code.toString()))
                    }
                    ErrorCode.ConnectionError -> {
                        StringWrap("Please connect to the network")
                    }
                    else -> {
                        StringWrap("Unknown Error")
                    }
                }
            )
        }
    }

    override fun onLoadingCountChanged() {
        _showLoadingSpinner.value = loadingCount.get() != 0
    }
}


sealed class GistListUIItem{
    abstract val username: String
    abstract val id: String

    data class Gist(
        override val username: String,
        override val id: String,
        val idText: TextWrap,
        val url: TextWrap,
        val csvFileName: TextWrap = StringWrap(),
        val isFavorite: Boolean = false
    ): GistListUIItem()

    data class UserInfo(
        override val username: String,
        override val id: String,
        val info: TextWrap
    ): GistListUIItem()
}