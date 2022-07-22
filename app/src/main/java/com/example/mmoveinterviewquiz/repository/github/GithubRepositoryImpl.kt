package com.example.mmoveinterviewquiz.repository.github

import com.example.mmoveinterviewquiz.data.local.dao.FavoriteDao
import com.example.mmoveinterviewquiz.data.local.entity.Favorite
import com.example.mmoveinterviewquiz.data.network.model.GetGistsResponseItem
import com.example.mmoveinterviewquiz.data.network.service.GithubApiService
import com.example.mmoveinterviewquiz.repository.BaseRepository
import com.example.mmoveinterviewquiz.repository.model.Gist
import com.example.mmoveinterviewquiz.repository.model.RepositoryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay

class GithubRepositoryImpl(private val apiService: GithubApiService, private val favoriteDao: FavoriteDao): BaseRepository() {


    suspend fun fetchGistsAsync(coroutineScope: CoroutineScope): Deferred<RepositoryResult<List<Gist>>> {
        return executeAsyncCall(coroutineScope) {
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


    suspend fun addFavoriteAsync(coroutineScope: CoroutineScope, gistID: String): Deferred<RepositoryResult<List<String>>> {
        return executeAsyncCall(coroutineScope) {
            favoriteDao.insert(favorite = Favorite(gistID))
            favoriteDao.getAll().map {
                it.id
            }
        }
    }

    suspend fun deleteFavoriteAsync(coroutineScope: CoroutineScope, gistID: String): Deferred<RepositoryResult<List<String>>> {
        return executeAsyncCall(coroutineScope) {
            favoriteDao.delete(favorite = Favorite(gistID))
            favoriteDao.getAll().map {
                it.id
            }
        }
    }

    suspend fun fetchFavoritesAsync(coroutineScope: CoroutineScope): Deferred<RepositoryResult<List<String>>> {
        return executeAsyncCall(coroutineScope) {
            favoriteDao.getAll().map {
                it.id
            }
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