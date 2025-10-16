package com.example.dzandroid.data.models.api

import com.google.gson.annotations.SerializedName

/**
 * Ответ от GitHub API для репозитория
 */
data class GithubRepoResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("owner") val owner: Owner,
    @SerializedName("description") val description: String?,
    @SerializedName("stargazers_count") val stars: Int,
    @SerializedName("forks_count") val forks: Int,
    @SerializedName("language") val language: String?,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("license") val license: License?,
    @SerializedName("topics") val topics: List<String>?,
    @SerializedName("html_url") val htmlUrl: String
)

data class Owner(
    @SerializedName("login") val login: String,
    @SerializedName("avatar_url") val avatarUrl: String?
)

data class License(
    @SerializedName("name") val name: String
)

/**
 * Ответ для поиска репозиториев
 */
data class SearchResponse(
    @SerializedName("items") val items: List<GithubRepoResponse>,
    @SerializedName("total_count") val totalCount: Int
)

/**
 * Ответ для README
 */
data class ReadmeResponse(
    @SerializedName("content") val content: String,
    @SerializedName("encoding") val encoding: String
)