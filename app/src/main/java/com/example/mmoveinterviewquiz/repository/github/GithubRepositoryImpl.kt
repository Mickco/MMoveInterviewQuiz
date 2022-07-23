package com.example.mmoveinterviewquiz.repository.github

import com.example.mmoveinterviewquiz.data.local.dao.FavoriteDao
import com.example.mmoveinterviewquiz.data.local.entity.Favorite
import com.example.mmoveinterviewquiz.data.network.model.FileType
import com.example.mmoveinterviewquiz.data.network.model.GetGistsResponseItem
import com.example.mmoveinterviewquiz.data.network.service.GithubApiService
import com.example.mmoveinterviewquiz.repository.BaseRepository
import com.example.mmoveinterviewquiz.repository.model.Gist
import com.example.mmoveinterviewquiz.repository.model.RepositoryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred

class GithubRepositoryImpl(private val apiService: GithubApiService, private val favoriteDao: FavoriteDao): BaseRepository(), GithubRepository {


    override suspend fun fetchGistsAsync(coroutineScope: CoroutineScope): Deferred<RepositoryResult<List<Gist>>> {
        return executeAsyncCall(coroutineScope) {
            val apiResult = apiService.getGists()

            apiResult.map {
                it.toGist()
            }
        }
    }

    override suspend fun fetchUserGistsAsync(coroutineScope: CoroutineScope, username: String): Deferred<RepositoryResult<List<Gist>>> {
        return executeAsyncCall(coroutineScope) {
            val apiResult = apiService.getUserGists(username)

            apiResult.map {
                it.toGist()
            }

        }

    }


    override suspend fun addFavoriteAsync(coroutineScope: CoroutineScope, gistID: String): Deferred<RepositoryResult<List<String>>> {
        return executeAsyncCall(coroutineScope) {
            favoriteDao.insert(favorite = Favorite(gistID))
            favoriteDao.getAll().map {
                it.id
            }
        }
    }

    override suspend fun deleteFavoriteAsync(coroutineScope: CoroutineScope, gistID: String): Deferred<RepositoryResult<List<String>>> {
        return executeAsyncCall(coroutineScope) {
            favoriteDao.delete(favorite = Favorite(gistID))
            favoriteDao.getAll().map {
                it.id
            }
        }
    }

    override suspend fun fetchFavoritesAsync(coroutineScope: CoroutineScope): Deferred<RepositoryResult<List<String>>> {
        return executeAsyncCall(coroutineScope) {
            favoriteDao.getAll().map {
                it.id
            }
        }
    }

    private fun GetGistsResponseItem.toGist(): Gist {
        return Gist(
            id = id,
            url = url,
            username = owner.login,
            csvFilename = files.values.firstOrNull { it.type == FileType.CSV }?.filename
        )
    }
}

