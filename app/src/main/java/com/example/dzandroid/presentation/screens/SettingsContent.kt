package com.example.dzandroid.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.dzandroid.presentation.RepoViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(viewModel: RepoViewModel) {
    val language by viewModel.languageFlow.collectAsState()
    val minRating by viewModel.minRatingFlow.collectAsState()
    val recipeName by viewModel.recipeNameFlow.collectAsState()

    var languageInput by remember { mutableStateOf(language) }
    var minRatingInput by remember { mutableStateOf(minRating.toString()) }
    var recipeNameInput by remember { mutableStateOf(recipeName) }

    LaunchedEffect(languageInput) {
        if (languageInput != language) {
            delay(500)
            viewModel.saveFiltersImmediately(languageInput, minRating, recipeName)
        }
    }

    LaunchedEffect(minRatingInput) {
        val minRatingInt = minRatingInput.toIntOrNull() ?: 0
        if (minRatingInt != minRating) {
            delay(500)
            viewModel.saveFiltersImmediately(language, minRatingInt, recipeName)
        }
    }

    LaunchedEffect(recipeNameInput) {
        if (recipeNameInput != recipeName) {
            delay(500)
            viewModel.saveFiltersImmediately(language, minRating, recipeNameInput)
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Настройки фильтра", style = MaterialTheme.typography.headlineMedium)

        Text("Настройки сохраняются сразу при вводе",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary)

        OutlinedTextField(
            value = languageInput,
            onValueChange = { languageInput = it },
            label = { Text("Язык программирования (например: Kotlin)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = minRatingInput,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) minRatingInput = newValue
            },
            label = { Text("Минимальное количество звезд") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = recipeNameInput,
            onValueChange = { recipeNameInput = it },
            label = { Text("Название репозитория") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val minRatingInt = minRatingInput.toIntOrNull() ?: 0
                viewModel.saveFiltersImmediately(languageInput, minRatingInt, recipeNameInput)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Применить фильтры")
        }
    }
}