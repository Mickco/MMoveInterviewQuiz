package com.example.mmoveinterviewquiz.repository.github

import com.example.mmoveinterviewquiz.repository.model.Gist
import com.example.mmoveinterviewquiz.repository.model.RepositoryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow


interface GithubRepository {
    val favoriteListFlow: Flow<RepositoryResult<List<String>>>

    suspend fun fetchGistsAsync(coroutineScope: CoroutineScope): Deferred<RepositoryResult<List<Gist>>>
    suspend fun fetchUserGistsAsync(coroutineScope: CoroutineScope, username: String): Deferred<RepositoryResult<List<Gist>>>
    suspend fun addFavoriteAsync(coroutineScope: CoroutineScope, gistID: String): Deferred<RepositoryResult<Unit>>
    suspend fun deleteFavoriteAsync(coroutineScope: CoroutineScope, gistID: String): Deferred<RepositoryResult<Unit>>
}