package com.example.dzandroid.data.repository

import com.example.dzandroid.data.mapper.decodeReadmeContent
import com.example.dzandroid.data.mapper.toDomain
import com.example.dzandroid.data.models.Repository
import com.example.dzandroid.data.remote.api.GithubApi
import com.example.dzandroid.data.remote.result.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GithubRepositoryImpl(
    private val githubApi: GithubApi
) : GithubRepository {

    override suspend fun searchRepositories(
        query: String,
        sort: String,
        order: String,
        perPage: Int,
        page: Int
    ): ApiResult<List<Repository>> = withContext(Dispatchers.IO) {
        try {
            val response = githubApi.searchRepositories(query, sort, order, perPage, page)
            if (response.isSuccessful) {
                val repositories = response.body()?.items?.map { it.toDomain() } ?: emptyList()
                ApiResult.Success(repositories)
            } else {
                ApiResult.Error("Ошибка при загрузке репозиториев: ${response.code()}")
            }
        } catch (e: Exception) {
            ApiResult.Error("Сетевая ошибка: ${e.message}")
        }
    }

    override suspend fun getRepository(owner: String, repo: String): ApiResult<Repository> =
        withContext(Dispatchers.IO) {
            try {
                val response = githubApi.getRepository(owner, repo)
                if (response.isSuccessful) {
                    val repository = response.body()?.toDomain()
                        ?: return@withContext ApiResult.Error("Репозиторий не найден")
                    ApiResult.Success(repository)
                } else {
                    ApiResult.Error("Ошибка при загрузке репозитория: ${response.code()}")
                }
            } catch (e: Exception) {
                ApiResult.Error("Сетевая ошибка: ${e.message}")
            }
        }

    override suspend fun getReadme(owner: String, repo: String): ApiResult<String> =
        withContext(Dispatchers.IO) {
            try {
                val response = githubApi.getReadme(owner, repo)
                if (response.isSuccessful) {
                    val readmeContent = response.body()?.content
                        ?: return@withContext ApiResult.Error("README не найден")
                    val decodedContent = decodeReadmeContent(readmeContent)
                    ApiResult.Success(decodedContent)
                } else {
                    ApiResult.Error("Ошибка при загрузке README: ${response.code()}")
                }
            } catch (e: Exception) {
                ApiResult.Error("Сетевая ошибка при загрузке README: ${e.message}")
            }
        }
}