package com.example.mmoveinterviewquiz.repository.github

import com.example.mmoveinterviewquiz.network.service.GithubApiService
import com.example.mmoveinterviewquiz.repository.BaseRepository
import com.example.mmoveinterviewquiz.repository.model.Gist
import com.mickco.assigment9gag.repository.model.RepositoryResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GithubRepositoryImpl(private val apiService: GithubApiService): BaseRepository(), GithubRepository {


    override suspend fun fetchGists(): RepositoryResult<List<Gist>> {
        return withContext(Dispatchers.IO) {
            executeApiCall {
                val apiResult = apiService.getGists()

                apiResult.map {
                    Gist(
                        id = it.id,
                        url = it.url,
                        csvFilename = null
                    )
                }
            }
        }
    }

    override suspend fun updateFavorite(id: String): RepositoryResult<List<String>> {
        return RepositoryResult.Success(listOf("1234"))
    }

    override suspend fun fetchFavorites(): RepositoryResult<List<String>> {
        return RepositoryResult.Success(listOf("1234"))
    }
}

interface GithubRepository {
    suspend fun fetchGists(): RepositoryResult<List<Gist>>
    suspend fun fetchFavorites(): RepositoryResult<List<String>>
    suspend fun updateFavorite(id: String): RepositoryResult<List<String>>
}