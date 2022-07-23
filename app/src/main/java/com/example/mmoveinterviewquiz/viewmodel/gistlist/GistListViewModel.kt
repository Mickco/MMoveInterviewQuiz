package com.example.mmoveinterviewquiz.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.mmoveinterviewquiz.R
import com.example.mmoveinterviewquiz.repository.github.GithubRepository
import com.example.mmoveinterviewquiz.repository.model.ErrorCode
import com.example.mmoveinterviewquiz.repository.model.Gist
import com.example.mmoveinterviewquiz.repository.model.RepositoryResult
import com.example.mmoveinterviewquiz.util.safeSubList
import com.example.mmoveinterviewquiz.view.common.FormatWrap
import com.example.mmoveinterviewquiz.view.common.ResourceWrap
import com.example.mmoveinterviewquiz.view.common.StringWrap
import com.example.mmoveinterviewquiz.view.common.TextWrap
import com.example.mmoveinterviewquiz.viewmodel.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GistListViewModel @Inject constructor(private val repository: GithubRepository): BaseViewModel() {

    companion object {
        private const val USER_GISTS_THRESHOLD = 5
        private const val BATCH_LOADING_SIZE = 4
    }

    private var _gistList = listOf<Gist>()
    private var _lastfetchedUserInfoIdx = 0
    private var _fetchedUsernameHistory = mutableListOf<String>()
    private val _gistListUIModel= MutableStateFlow(listOf<GistListUIItem>())
    private val _showLoadingSpinner = MutableStateFlow(false)
    private val _snackbarMessage = MutableSharedFlow<TextWrap>(replay = 0)
    private val _navigateToDetail = MutableSharedFlow<Gist>(replay = 0)
    private val _favoriteList = repository.favoriteListFlow.onEach { favList ->
        _gistListUIModel.value = _gistListUIModel.value.map {
            when(it) {
                is GistListUIItem.Gist -> it.copy(isFavorite = it.id in favList)
                else -> it
            }
        }
    }.stateIn(viewModelScope, started = SharingStarted.Eagerly, listOf())

    val gistListUIModel:StateFlow<List<GistListUIItem>> = _gistListUIModel
    val showLoadingSpinner: StateFlow<Boolean> = _showLoadingSpinner
    val snackbarMessage: Flow<TextWrap> = _snackbarMessage
    val navigateToDetail: Flow<Gist> = _navigateToDetail


    fun onClickFavoriteGist(gistId: String) {
        launchLoadingScope {
            val isFavorite = gistId in _favoriteList.value
            val res = when (isFavorite) {
                true -> repository.deleteFavoriteAsync(this, gistId)
                false -> repository.addFavoriteAsync(this, gistId)
            }.await()

            when (res) {
                is RepositoryResult.Fail -> {
                    onResponseFail(res)
                }
            }
        }

    }

    fun onClickItem(gistId: String) {
        launchLoadingScope {
            val clickedItem = _gistList.firstOrNull{it.id == gistId}  ?: return@launchLoadingScope
            _navigateToDetail.emit(clickedItem)
        }
    }

    fun initViewModel() {
        launchLoadingScope {
            val fetchGistsReq = repository.fetchGistsAsync(this)
            val fetchGistRes = fetchGistsReq.await()

            when {
                fetchGistRes is RepositoryResult.Success  -> {
                    _gistList = fetchGistRes.data
                    val gistsUIList = fetchGistRes.data.map {
                        GistListUIItem.Gist(
                            id = it.id,
                            username = it.username,
                            idText = StringWrap(it.id),
                            url = StringWrap(it.url),
                            isFavorite = it.id in _favoriteList.value,
                            csvFileName = StringWrap(it.csvFilename.orEmpty()),
                        )
                    }
                    _gistListUIModel.value = gistsUIList
                }
                fetchGistRes is RepositoryResult.Fail -> {
                    onResponseFail(fetchGistRes)
                }
            }
        }
    }

    fun onScrollLoadMoreUserInfo(lastCompletelyVisibleItemPosition: Int) {
        if (loadingCount.get() != 0) return
        val lastVisibleGistIdx = _gistListUIModel.value.subList(0, lastCompletelyVisibleItemPosition).filterIsInstance<GistListUIItem.Gist>().lastIndex
        if (lastVisibleGistIdx < _lastfetchedUserInfoIdx) return
        launchLoadingScope {
            val usernames = _gistListUIModel.value
                .filterIsInstance<GistListUIItem.Gist>()
                .safeSubList(_lastfetchedUserInfoIdx, _lastfetchedUserInfoIdx+BATCH_LOADING_SIZE)
                .map {
                it.username
            }

            val userGistsResultList = usernames
                .distinct()
                .filter { it !in _fetchedUsernameHistory }
                .map {
                repository.fetchUserGistsAsync(this, it)
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
                                        ResourceWrap(R.string.user_info_message),
                                        StringWrap(it.username),
                                        StringWrap(gistsCount.toString()))
                                )
                            )
                        }
                    }
                    _fetchedUsernameHistory.addAll(userGistsCountMap.keys)
                    _lastfetchedUserInfoIdx += BATCH_LOADING_SIZE
                    _gistListUIModel.value = newGistUIList
                }
                false -> {
                    val failResult = userGistsResultList.filterIsInstance<RepositoryResult.Fail>().first()
                    onResponseFail(failResult)
                }
            }
        }
    }


    private fun onResponseFail(error: RepositoryResult.Fail) {
        viewModelScope.launch(Dispatchers.Main) {
            _snackbarMessage.emit(
                when (error.errorCode) {
                    ErrorCode.HTTPError -> {
                        FormatWrap(ResourceWrap(R.string.http_error_message), StringWrap(error.errorMessage?.code.toString()))
                    }
                    ErrorCode.ConnectionError -> {
                        ResourceWrap(R.string.connection_error_message)
                    }
                    else -> {
                        ResourceWrap(R.string.unknown_error_message)
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