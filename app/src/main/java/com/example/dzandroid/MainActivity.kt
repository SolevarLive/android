package com.example.dzandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.dzandroid.navigation.MainApp
import com.example.dzandroid.ui.theme.GithubReposTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GithubReposTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: com.example.dzandroid.presentation.RepoViewModel = viewModel()

                    MainApp(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}