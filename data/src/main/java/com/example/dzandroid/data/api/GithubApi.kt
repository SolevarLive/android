package com.example.dzandroid.data.api

import com.example.dzandroid.data.models.api.GithubRepoResponse
import com.example.dzandroid.data.models.api.ReadmeResponse
import com.example.dzandroid.data.models.api.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {

    /**
     * Поиск репозиториев по запросу
     * @param query поисковый запрос
     * @param sort поле для сортировки (stars, forks, updated)
     * @param order направление сортировки (asc, desc)
     * @param perPage количество элементов на странице
     * @param page номер страницы
     */
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): Response<SearchResponse>

    /**
     * Получение информации о конкретном репозитории
     * @param owner владелец репозитория
     * @param repo название репозитория
     */
    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<GithubRepoResponse>

    /**
     * Получение README файла репозитория
     * @param owner владелец репозитория
     * @param repo название репозитория
     */
    @GET("repos/{owner}/{repo}/readme")
    suspend fun getReadme(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<ReadmeResponse>
}