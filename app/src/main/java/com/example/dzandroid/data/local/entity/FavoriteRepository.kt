package com.example.dzandroid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteRepository(
    @PrimaryKey val id: Long,
    val name: String,
    val owner: String,
    val description: String,
    val stars: Int,
    val forks: Int,
    val language: String,
    val updatedAt: String
)