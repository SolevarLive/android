package com.example.dzandroid.data.repository

import com.example.dzandroid.data.models.Repository
import com.example.dzandroid.data.remote.result.ApiResult

/**
 * Репозиторий для работы с данными GitHub
 * Определяет контракты для получения данных о репозиториях
 */
interface GithubRepository {
    /**
     * Выполняет поиск репозиториев по заданному запросу
     *
     * @param query поисковый запрос
     * @param sort поле для сортировки (stars, forks, updated)
     * @param order направление сортировки (asc, desc)
     * @param perPage количество элементов на странице
     * @param page номер страницы
     * @return результат операции с списком репозиториев
     */
    suspend fun searchRepositories(
        query: String,
        sort: String,
        order: String,
        perPage: Int,
        page: Int
    ): ApiResult<List<Repository>>

    /**
     * Получает детальную информацию о конкретном репозитории
     *
     * @param owner логин владельца репозитория
     * @param repo название репозитория
     * @return результат операции с данными репозитория
     */
    suspend fun getRepository(owner: String, repo: String): ApiResult<Repository>

    /**
     * Загружает содержимое README файла репозитория
     *
     * @param owner логин владельца репозитория
     * @param repo название репозитория
     * @return результат операции с содержимым README
     */
    suspend fun getReadme(owner: String, repo: String): ApiResult<String>
}