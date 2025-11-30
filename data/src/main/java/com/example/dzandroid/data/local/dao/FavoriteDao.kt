package com.example.dzandroid.data.local.dao

import androidx.room.*
import com.example.dzandroid.data.local.entity.FavoriteRepository
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites")
    fun getAll(): Flow<List<FavoriteRepository>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteRepository)

    @Delete
    suspend fun delete(favorite: FavoriteRepository)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    suspend fun isFavorite(id: Long): Boolean

    @Query("SELECT * FROM favorites WHERE id = :id")
    suspend fun getById(id: Long): FavoriteRepository?
}