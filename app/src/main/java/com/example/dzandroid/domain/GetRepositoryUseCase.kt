package com.example.dzandroid.domain

import com.example.dzandroid.data.models.Repository
import com.example.dzandroid.data.repository.GithubRepository
import com.example.dzandroid.data.remote.result.ApiResult

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