package com.example.dzandroid.data.models

/**
 * Модель данных профиля пользователя
 */
data class Profile(
    val id: Long = 1,
    val fullName: String = "",
    val avatarUri: String = "",
    val resumeUrl: String = "",
    val position: String = "",
    val email: String = "",
    val favoriteClassTime: String = ""
)