package com.example.mmoveinterviewquiz.repository.github

import android.util.Log
import com.example.mmoveinterviewquiz.network.model.GetGistsResponseItem
import com.example.mmoveinterviewquiz.network.service.GithubApiService
import com.example.mmoveinterviewquiz.repository.BaseRepository
import com.example.mmoveinterviewquiz.repository.model.Gist
import com.example.mmoveinterviewquiz.repository.model.RepositoryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred

class GithubRepositoryImpl(private val apiService: GithubApiService): BaseRepository() {


    suspend fun fetchGistsAsync(coroutineScope: CoroutineScope): Deferred<RepositoryResult<List<Gist>>> {
        return executeSuspendCall(coroutineScope) {
            val apiResult = apiService.getGists()

            apiResult.map {
                it.toGist()
            }
        }
    }




    suspend fun fetchUserGistsAsync(coroutineScope: CoroutineScope, username: String): Deferred<RepositoryResult<List<Gist>>> {
        return executeSuspendCall(coroutineScope) {
            val apiResult = apiService.getUserGists(username)

            apiResult.map {
                it.toGist()
            }
            list
        }

    }

    private fun GetGistsResponseItem.toGist(): Gist {
        return Gist(
            id = id,
            url = url,
            username = owner.login,
            csvFilename = null
        )
    }


    suspend fun updateFavoriteAsync(coroutineScope: CoroutineScope, id: String): Deferred<RepositoryResult<List<String>>> {
        return executeSuspendCall(coroutineScope) {
                listOf("1234")
        }
    }

    suspend fun fetchFavoritesAsync(coroutineScope: CoroutineScope): Deferred<RepositoryResult<List<String>>> {
        return executeSuspendCall(coroutineScope) {
            listOf("1234")
        }
    }
}
//
//interface GithubRepository {
//    suspend fun fetchGists(): RepositoryResult<List<Gist>>
//    suspend fun fetchUserGists(username: String):RepositoryResult<List<Gist>>
//    suspend fun fetchFavorites(): RepositoryResult<List<String>>
//    suspend fun updateFavorite(id: String): RepositoryResult<List<String>>
//}