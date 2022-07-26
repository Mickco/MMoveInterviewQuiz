package com.example.mmoveinterviewquiz.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mmoveinterviewquiz.data.local.entity.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite")
    fun getAll(): Flow<List<Favorite>>

    @Insert
    suspend fun insert(favorite: Favorite)

    @Delete
    suspend fun delete(favorite: Favorite)
}