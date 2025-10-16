package com.example.dzandroid.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dzandroid.data.remote.result.ApiResult

/**
 * Composable компонент для отображения состояний загрузки
 * Универсальный компонент показывает loading, error или контент
 *
 * @param state текущее состояние загрузки
 * @param onRetry callback для повторной попытки загрузки при ошибке
 * @param content composable контент для отображения при успешной загрузке
 */
@Composable
fun LoadingState(
    state: ApiResult<Unit>,
    onRetry: () -> Unit,
    content: @Composable () -> Unit
) {
    when (state) {
        is ApiResult.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Загрузка репозиториев...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        is ApiResult.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Ошибка загрузки",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onRetry) {
                    Text("Повторить")
                }
            }
        }
        is ApiResult.Success -> {
            content()
        }
    }
}