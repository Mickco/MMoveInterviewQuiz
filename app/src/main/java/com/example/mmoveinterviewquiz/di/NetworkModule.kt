package com.example.mmoveinterviewquiz.di

import android.content.Context
import com.example.mmoveinterviewquiz.BuildConstant
import com.example.mmoveinterviewquiz.network.interceptor.NetworkConnectionInterceptor
import com.example.mmoveinterviewquiz.network.service.GithubApiService
import com.example.mmoveinterviewquiz.repository.github.GithubRepository
import com.example.mmoveinterviewquiz.repository.github.GithubRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(NetworkConnectionInterceptor(context))
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConstant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideDemoTyiCodeApiService(
        retrofit: Retrofit
    ): GithubApiService {
        return retrofit.create(GithubApiService::class.java)
    }

    @Provides
    fun provideGithubRepository(
        apiService: GithubApiService
    ): GithubRepository {
        return GithubRepositoryImpl(
            apiService = apiService
        )
    }

}
