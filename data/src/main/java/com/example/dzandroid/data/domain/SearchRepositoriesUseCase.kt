package com.example.dzandroid.data.domain

import com.example.dzandroid.data.models.Repository
import com.example.dzandroid.data.repository.GithubRepository
import com.example.dzandroid.core.ApiResult

class SearchRepositoriesUseCase(
    private val repository: GithubRepository
) {
    suspend operator fun invoke(
        query: String = "android",
        sort: String = "stars",
        order: String = "desc",
        perPage: Int = 30,
        page: Int = 1
    ): ApiResult<List<Repository>> {
        return repository.searchRepositories(query, sort, order, perPage, page)
    }
}