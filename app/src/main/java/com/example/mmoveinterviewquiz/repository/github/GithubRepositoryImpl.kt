package com.example.mmoveinterviewquiz.repository.github

import com.example.mmoveinterviewquiz.network.model.GetGistsResponseItem
import com.example.mmoveinterviewquiz.network.service.GithubApiService
import com.example.mmoveinterviewquiz.repository.BaseRepository
import com.example.mmoveinterviewquiz.repository.model.Gist
import com.example.mmoveinterviewquiz.repository.model.RepositoryResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GithubRepositoryImpl(private val apiService: GithubApiService): BaseRepository() {


    suspend fun fetchGists(): RepositoryResult<List<Gist>> {
        return withContext(Dispatchers.IO) {
            executeApiCall {
                val apiResult = apiService.getGists()

                apiResult.map {
                    it.toGist()
                }
            }
        }
    }


    suspend fun fetchUserGists(username: String): RepositoryResult<List<Gist>> {
        return withContext(Dispatchers.IO) {
                executeApiCall {
                    val apiResult = apiService.getUserGists(username)

                    apiResult.map {
                        it.toGist()
                    }
            }

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


    suspend fun updateFavorite(id: String): RepositoryResult<List<String>> {
        return withContext(Dispatchers.IO) {
                RepositoryResult.Success(listOf("1234"))
        }
    }

    suspend fun fetchFavorites(): RepositoryResult<List<String>> {
        return withContext(Dispatchers.IO) {
                RepositoryResult.Success(listOf("1234"))
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