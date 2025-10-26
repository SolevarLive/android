package com.example.dzandroid.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dzandroid.data.local.entity.FavoriteRepository
import com.example.dzandroid.data.models.Repository

@Composable
fun FavoritesScreen(
    favorites: List<FavoriteRepository>,
    onRepoClick: (Repository) -> Unit
) {
    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Нет избранных репозиториев")
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(favorites) { favorite ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            val repo = Repository(
                                id = favorite.id,
                                name = favorite.name,
                                owner = favorite.owner,
                                description = favorite.description,
                                stars = favorite.stars,
                                forks = favorite.forks,
                                language = favorite.language,
                                updatedAt = favorite.updatedAt
                            )
                            onRepoClick(repo)
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${favorite.owner}/${favorite.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(favorite.description, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("★ ${favorite.stars}", style = MaterialTheme.typography.bodySmall)
                            Text("🍴 ${favorite.forks}", style = MaterialTheme.typography.bodySmall)
                            Text(favorite.language, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}