package com.example.dzandroid.navigation

/**
 * Sealed класс, представляющий все возможные экраны в приложении
 * Используется для навигации между экранами
 */

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object RepoDetails : Screen("repo_details")
}