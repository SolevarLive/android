package com.example.dzandroid.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dzandroid.presentation.components.RepoListItem
import com.example.dzandroid.presentation.RepoViewModel

/**
 * Главный экран приложения с BottomNavigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: RepoViewModel,
    onRepoClick: (com.example.dzandroid.data.models.Repository) -> Unit,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val repositories = viewModel.repositories.collectAsState().value

    val content: @Composable (PaddingValues) -> Unit = { paddingValues ->
        when (selectedTab) {
            0 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(repositories) { repo ->
                            RepoListItem(
                                repository = repo,
                                onClick = { onRepoClick(repo) }
                            )
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
                    ProfileContent()
                }
            }
            2 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    SettingsContent()
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
                            1 -> "Профиль"
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
                    icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
                    label = { Text("Профиль") },
                    selected = selectedTab == 1,
                    onClick = { onTabSelected(1) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Настройки") },
                    label = { Text("Настройки") },
                    selected = selectedTab == 2,
                    onClick = { onTabSelected(2) }
                )
            }
        },
        content = content
    )
}

/**
 * Контент для экрана профиля
 */
@Composable
private fun ProfileContent() {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SolevarLive",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Android разработчик",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Студент УрФУ",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Статистика",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Репозитории: 15",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Подписчики: 42",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Подписки: 23",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Контент для экрана настроек
 */
@Composable
private fun SettingsContent() {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Настройки",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Тема: Системная",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Язык: Русский",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Уведомления: Включены",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Это демонстрационный экран настроек",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}