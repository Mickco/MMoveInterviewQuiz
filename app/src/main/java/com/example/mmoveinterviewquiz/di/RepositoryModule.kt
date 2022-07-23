package com.example.mmoveinterviewquiz.di

import com.example.mmoveinterviewquiz.data.local.dao.FavoriteDao
import com.example.mmoveinterviewquiz.data.network.service.GithubApiService
import com.example.mmoveinterviewquiz.repository.github.GithubRepository
import com.example.mmoveinterviewquiz.repository.github.GithubRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    fun provideGithubRepository(
        apiService: GithubApiService,
        favoriteDao: FavoriteDao
    ): GithubRepository {
        return GithubRepositoryImpl(
            apiService = apiService,
            favoriteDao = favoriteDao
        )
    }
}