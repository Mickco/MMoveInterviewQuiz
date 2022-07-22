package com.example.mmoveinterviewquiz.network.service

import com.example.mmoveinterviewquiz.network.model.GetGistsResponseItem
import retrofit2.http.GET

interface GithubApiService {
    @GET("/gists/public?since")
    suspend fun getGists(): List<GetGistsResponseItem>
}
