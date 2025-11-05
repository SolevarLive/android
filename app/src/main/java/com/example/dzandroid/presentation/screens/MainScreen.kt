package com.example.dzandroid.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import androidx.navigation.NavController
import com.example.dzandroid.data.models.Repository
import com.example.dzandroid.di.FilterBadgeCache
import com.example.dzandroid.navigation.Screen
import com.example.dzandroid.presentation.ProfileViewModel
import com.example.dzandroid.presentation.RepoViewModel
import com.example.dzandroid.presentation.components.LoadingState
import com.example.dzandroid.presentation.components.RepoListItem

/**
 * Главный экран приложения с BottomNavigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: RepoViewModel,
    profileViewModel: ProfileViewModel,
    navController: NavController,
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
                    ProfileScreen(
                        viewModel = profileViewModel,
                        navController = navController,
                        onEditClick = {
                            navController.navigate(Screen.EditProfile.route)
                        }
                    )
                }
            }
            3 -> {
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
                            2 -> "Профиль"
                            3 -> "Фильтры"
                            else -> "Репозитории GitHub"
                        }
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Репозитории") },
                    label = { Text("Репозитории") },
                    selected = selectedTab == 0,
                    onClick = { onTabSelected(0) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Избранное") },
                    label = { Text("Избранное") },
                    selected = selectedTab == 1,
                    onClick = { onTabSelected(1) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
                    label = { Text("Профиль") },
                    selected = selectedTab == 2,
                    onClick = { onTabSelected(2) }
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
                            Icon(Icons.Default.FilterList, contentDescription = "Фильтры")
                        }
                    },
                    label = { Text("Фильтры") },
                    selected = selectedTab == 3,
                    onClick = { onTabSelected(3) }
                )
            }
        },
        content = content
    )
}