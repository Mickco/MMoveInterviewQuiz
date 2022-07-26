package com.example.mmoveinterviewquiz.data.network.service

import com.example.mmoveinterviewquiz.data.network.model.GetGistsResponseItem
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApiService {
    @GET("/gists/public?since")
    suspend fun getGists(): List<GetGistsResponseItem>

    @GET("/users/{username}/gists?since")
    suspend fun getUserGists(@Path("username",encoded = true) username: String): List<GetGistsResponseItem>
}
