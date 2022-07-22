package com.example.mmoveinterviewquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mmoveinterviewquiz.repository.github.GithubRepository
import com.example.mmoveinterviewquiz.view.common.StringWrap
import com.example.mmoveinterviewquiz.view.common.TextWrap
import com.mickco.assigment9gag.repository.model.RepositoryResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GistListViewModel @Inject constructor(private val repository: GithubRepository): ViewModel() {

    private val _gistListUIModel= MutableStateFlow(GistListUIModel())
    val gistListUIModel:StateFlow<GistListUIModel> = _gistListUIModel



    fun initViewModel() {
        viewModelScope.launch(Dispatchers.Main) {
            val fetchGistsReq = async { repository.fetchGists() }
            val fetchFavoritesReq = async { repository.fetchFavorites() }
            val fetchGistRes = fetchGistsReq.await()
            val fetchFavRes = fetchFavoritesReq.await()

            when {
                fetchGistRes is RepositoryResult.Success && fetchFavRes is RepositoryResult.Success -> {
                    val uiModel = _gistListUIModel.value
                    val favList = fetchFavRes.data
                    val gistsUIList = fetchGistRes.data.map {
                        GistListItem.Gist(
                            id = it.id,
                            idText = StringWrap(it.id),
                            url = StringWrap(it.url),
                            isFavorite = it.id in favList,
                            csvFileName = StringWrap("TODO"),
                        )
                    }
                    _gistListUIModel.value = uiModel.copy(
                        gistList = gistsUIList
                    )
                }
                else -> {
                    onResponseFail()
                }
            }


        }

    }

    private fun onResponseFail() {

    }

    private fun updateOnFavoriteListResponse(res: RepositoryResult<List<String>>) {
        when (res) {
            is RepositoryResult.Success -> {
                val uiModel = _gistListUIModel.value
                val favList = res.data
                val gistListWithFav = uiModel.gistList.map {
                    if (it is GistListItem.Gist) {
                        it.copy(isFavorite = it.id in favList)
                    } else {
                        it
                    }
                }
                _gistListUIModel.value = uiModel.copy(
                    gistList = gistListWithFav
                )
            }
            is RepositoryResult.Fail -> {

            }
        }

    }

}

data class GistListUIModel(
    val gistList: List<GistListItem> = listOf()
)

sealed class GistListItem{
    data class Gist(
        val id: String,
        val idText: TextWrap,
        val url: TextWrap,
        val csvFileName: TextWrap = StringWrap(),
        val isFavorite: Boolean = false
    ): GistListItem()
}