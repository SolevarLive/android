package com.example.dzandroid.data.models

/**
 * Модель данных, представляющая репозиторий GitHub
 */
data class Repository(
    val id: Long,
    val name: String,
    val owner: String,
    val description: String,
    val stars: Int,
    val forks: Int,
    val language: String,
    val updatedAt: String,
    val license: String? = null,
    val topics: List<String> = emptyList(),
    val readme: String = ""
)