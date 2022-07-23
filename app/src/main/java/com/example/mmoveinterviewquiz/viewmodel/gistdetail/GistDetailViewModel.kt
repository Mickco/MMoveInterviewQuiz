package com.example.mmoveinterviewquiz.viewmodel.gistdetail

import com.example.mmoveinterviewquiz.repository.github.GithubRepository
import com.example.mmoveinterviewquiz.repository.model.Gist
import com.example.mmoveinterviewquiz.repository.model.RepositoryResult
import com.example.mmoveinterviewquiz.view.common.StringWrap
import com.example.mmoveinterviewquiz.view.common.TextWrap
import com.example.mmoveinterviewquiz.viewmodel.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GistDetailViewModel @Inject constructor(private val repository: GithubRepository) : BaseViewModel() {

    private lateinit var _selectedGist: Gist
    private val _isFavorite = MutableStateFlow(false)
    private val _displayText = MutableStateFlow<TextWrap>(StringWrap())

    val isFavorite: StateFlow<Boolean> = _isFavorite
    val displayText: StateFlow<TextWrap> = _displayText
    var isFavChanged: Boolean = false
        private set

    fun initViewModel(gist: Gist) {
        _selectedGist = gist
        _displayText.value = StringWrap(gist.toString())

        launchLoadingScope {
            val res = repository.fetchFavoritesAsync(this).await()

            if (res is RepositoryResult.Success) {
                _isFavorite.value = gist.id in res.data
            }
        }
    }

    fun onClickFavorite() {
        launchLoadingScope {
            val gistId = _selectedGist.id
            val res = when (_isFavorite.value) {
                true -> repository.deleteFavoriteAsync(this, gistId)
                false -> repository.addFavoriteAsync(this, gistId)
            }.await()

            if (res is RepositoryResult.Success) {
                isFavChanged = true
                _isFavorite.value = gistId in res.data
            }
        }
    }
}