package com.example.dzandroid.data.domain

import com.example.dzandroid.data.repository.GithubRepository
import com.example.dzandroid.core.ApiResult


/**
 * UseCase для загрузки README файла репозитория
 * Инкапсулирует бизнес-логику получения содержимого README
 */
class GetReadmeUseCase(
    private val repository: GithubRepository
) {
    suspend operator fun invoke(owner: String, repo: String): ApiResult<String> {
        return repository.getReadme(owner, repo)
    }
}