package com.example.mmoveinterviewquiz.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mmoveinterviewquiz.data.local.dao.FavoriteDao
import com.example.mmoveinterviewquiz.data.local.entity.Favorite


@Database(entities = [Favorite::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}