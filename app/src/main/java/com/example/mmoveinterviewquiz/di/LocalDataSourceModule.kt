package com.example.mmoveinterviewquiz.di

import android.content.Context
import androidx.room.Room
import com.example.mmoveinterviewquiz.BuildConstant.APP_DATABASE_NAME
import com.example.mmoveinterviewquiz.data.local.AppDatabase
import com.example.mmoveinterviewquiz.data.local.dao.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LocalDataSourceModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, APP_DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideFavoriteDao(
        appDatabase: AppDatabase
    ): FavoriteDao {
        return appDatabase.favoriteDao()
    }

}