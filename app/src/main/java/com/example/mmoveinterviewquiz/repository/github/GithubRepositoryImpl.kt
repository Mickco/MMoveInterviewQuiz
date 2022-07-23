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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GithubRepositoryImpl(private val apiService: GithubApiService, private val favoriteDao: FavoriteDao): BaseRepository(), GithubRepository {

    override val favoriteListFlow: Flow<RepositoryResult<List<String>>> = favoriteDao.getAll().map {
        RepositoryResult.Success(it.map { it.id }) as RepositoryResult<List<String>>
    }.catch { e ->
        emit(handleException(e) )
    }

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


    override suspend fun addFavoriteAsync(coroutineScope: CoroutineScope, gistID: String): Deferred<RepositoryResult<Unit>> {
        return executeAsyncCall(coroutineScope) {
            favoriteDao.insert(favorite = Favorite(gistID))
        }
    }

    override suspend fun deleteFavoriteAsync(coroutineScope: CoroutineScope, gistID: String): Deferred<RepositoryResult<Unit>>{
        return executeAsyncCall(coroutineScope) {
            favoriteDao.delete(favorite = Favorite(gistID))
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

