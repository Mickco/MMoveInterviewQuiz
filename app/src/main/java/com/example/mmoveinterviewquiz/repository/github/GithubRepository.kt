package com.example.mmoveinterviewquiz.repository.github

import com.example.mmoveinterviewquiz.repository.model.Gist
import com.example.mmoveinterviewquiz.repository.model.RepositoryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred


interface GithubRepository {
    suspend fun fetchGistsAsync(coroutineScope: CoroutineScope): Deferred<RepositoryResult<List<Gist>>>
    suspend fun fetchUserGistsAsync(coroutineScope: CoroutineScope, username: String): Deferred<RepositoryResult<List<Gist>>>
    suspend fun addFavoriteAsync(coroutineScope: CoroutineScope, gistID: String): Deferred<RepositoryResult<List<String>>>
    suspend fun deleteFavoriteAsync(coroutineScope: CoroutineScope, gistID: String): Deferred<RepositoryResult<List<String>>>
    suspend fun fetchFavoritesAsync(coroutineScope: CoroutineScope): Deferred<RepositoryResult<List<String>>>
}