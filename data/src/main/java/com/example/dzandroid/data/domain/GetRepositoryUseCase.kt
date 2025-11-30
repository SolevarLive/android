package com.example.dzandroid.data.domain

import com.example.dzandroid.core.ApiResult
import com.example.dzandroid.data.models.Repository
import com.example.dzandroid.data.repository.GithubRepository

/**
 * UseCase для получения детальной информации о репозитории
 * Инкапсулирует бизнес-логику загрузки данных репозитория
 */
class GetRepositoryUseCase(
    private val repository: GithubRepository
) {
    suspend operator fun invoke(owner: String, repo: String): ApiResult<Repository> {
        return repository.getRepository(owner, repo)
    }
}