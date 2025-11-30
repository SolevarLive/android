package com.example.dzandroid.data.mapper

import android.util.Base64
import com.example.dzandroid.data.models.Repository
import com.example.dzandroid.data.models.api.GithubRepoResponse

/**
 * Преобразует ответ API GitHub в доменную модель Repository
 */
fun GithubRepoResponse.toDomain(): Repository {
    return Repository(
        id = id,
        name = name,
        owner = owner.login,
        description = description ?: "Описание отсутствует",
        stars = stars,
        forks = forks,
        language = language ?: "Не указан",
        updatedAt = updatedAt.substring(0, 10),
        license = license?.name,
        topics = topics ?: emptyList(),
        readme = "Загрузка README..."
    )
}

/**
 * Декодирует base64-encoded содержимое README файла
 */
fun decodeReadmeContent(encodedContent: String): String {
    return try {
        val decodedBytes = Base64.decode(encodedContent, Base64.DEFAULT)
        String(decodedBytes)
    } catch (e: Exception) {
        "Не удалось декодировать README: ${e.message}"
    }
}