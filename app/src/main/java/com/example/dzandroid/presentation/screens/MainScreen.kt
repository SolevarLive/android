package com.example.dzandroid.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dzandroid.data.models.Repository
import com.example.dzandroid.di.FilterBadgeCache
import com.example.dzandroid.presentation.components.LoadingState
import com.example.dzandroid.presentation.components.RepoListItem
import com.example.dzandroid.presentation.RepoViewModel

/**
 * Главный экран приложения с BottomNavigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: RepoViewModel,
    onRepoClick: (Repository) -> Unit,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    filterBadgeCache: FilterBadgeCache
) {
    val repositories = viewModel.repositories.collectAsState().value
    val loadingState = viewModel.loadingState.collectAsState().value
    val favorites = viewModel.favorites.collectAsState().value

    val content: @Composable (PaddingValues) -> Unit = { paddingValues ->
        when (selectedTab) {
            0 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LoadingState(
                        state = loadingState,
                        onRetry = { viewModel.retryLoading() }
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(repositories) { repo ->
                                RepoListItem(
                                    repository = repo,
                                    onClick = { onRepoClick(repo) },
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
            1 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    FavoritesScreen(
                        favorites = favorites,
                        onRepoClick = onRepoClick
                    )
                }
            }
            2 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    SettingsContent(viewModel = viewModel)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (selectedTab) {
                            0 -> "Репозитории GitHub"
                            1 -> "Избранное"
                            2 -> "Настройки"
                            else -> "Репозитории GitHub"
                        }
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Репозитории") },
                    label = { Text("Репозитории") },
                    selected = selectedTab == 0,
                    onClick = { onTabSelected(0) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Избранное") },
                    label = { Text("Избранное") },
                    selected = selectedTab == 1,
                    onClick = { onTabSelected(1) }
                )
                NavigationBarItem(
                    icon = {
                        BadgedBox(
                            badge = {
                                if (filterBadgeCache.showBadge) {
                                    Badge()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Настройки")
                        }
                    },
                    label = { Text("Настройки") },
                    selected = selectedTab == 2,
                    onClick = { onTabSelected(2) }
                )
            }
        },
        content = content
    )
}