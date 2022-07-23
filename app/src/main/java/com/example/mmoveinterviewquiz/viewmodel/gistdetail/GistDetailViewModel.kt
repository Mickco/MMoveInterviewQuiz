package com.example.mmoveinterviewquiz.viewmodel.gistdetail

import androidx.lifecycle.viewModelScope
import com.example.mmoveinterviewquiz.repository.github.GithubRepository
import com.example.mmoveinterviewquiz.repository.model.Gist
import com.example.mmoveinterviewquiz.repository.model.RepositoryResult
import com.example.mmoveinterviewquiz.view.common.StringWrap
import com.example.mmoveinterviewquiz.view.common.TextWrap
import com.example.mmoveinterviewquiz.viewmodel.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class GistDetailViewModel @Inject constructor(private val repository: GithubRepository) : BaseViewModel() {

    private lateinit var _selectedGist: Gist
    private val _isFavorite = MutableStateFlow(false)
    private val _displayText = MutableStateFlow<TextWrap>(StringWrap())
    private val _favoriteList: StateFlow<List<String>> = repository.favoriteListFlow
        .filterIsInstance<RepositoryResult.Success<List<String>>>()
        .map { it.data }
        .onEach { favList ->
            _isFavorite.value = _selectedGist.id in favList
    }.stateIn(viewModelScope, started = SharingStarted.Eagerly, listOf())

    val isFavorite: StateFlow<Boolean> = _isFavorite
    val displayText: StateFlow<TextWrap> = _displayText

    fun initViewModel(gist: Gist) {
        _selectedGist = gist
        _displayText.value = StringWrap(gist.toString())
        _isFavorite.value = gist.id in _favoriteList.value
    }

    fun onClickFavorite() {
        launchLoadingScope {
            val gistId = _selectedGist.id
            val isFavorite = gistId in _favoriteList.value
            val res = when (isFavorite) {
                true -> repository.deleteFavoriteAsync(this, gistId)
                false -> repository.addFavoriteAsync(this, gistId)
            }.await()
        }
    }
}