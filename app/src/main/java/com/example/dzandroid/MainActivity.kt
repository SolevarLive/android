package com.example.dzandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.dzandroid.data.local.database.AppDatabase
import com.example.dzandroid.data.local.datastore.SharedPrefsManager
import com.example.dzandroid.data.remote.api.GithubApi
import com.example.dzandroid.data.repository.GithubRepositoryImpl
import com.example.dzandroid.di.FilterBadgeCache
import com.example.dzandroid.domain.GetReadmeUseCase
import com.example.dzandroid.domain.GetRepositoryUseCase
import com.example.dzandroid.domain.SearchRepositoriesUseCase
import com.example.dzandroid.navigation.MainApp
import com.example.dzandroid.presentation.RepoViewModel
import com.example.dzandroid.ui.theme.GithubReposTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getInstance(this)

        val sharedPrefsManager = SharedPrefsManager(this)

        val filterBadgeCache = FilterBadgeCache()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val githubApi = retrofit.create(GithubApi::class.java)
        val repository = GithubRepositoryImpl(githubApi)
        val searchUseCase = SearchRepositoriesUseCase(repository)
        val getRepoUseCase = GetRepositoryUseCase(repository)
        val getReadmeUseCase = GetReadmeUseCase(repository)

        val viewModel = RepoViewModel(
            searchRepositoriesUseCase = searchUseCase,
            getRepositoryUseCase = getRepoUseCase,
            getReadmeUseCase = getReadmeUseCase,
            sharedPrefsManager = sharedPrefsManager,
            favoriteDao = database.favoriteDao(),
            filterBadgeCache = filterBadgeCache
        )

        setContent {
            GithubReposTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    MainApp(
                        navController = navController,
                        viewModel = viewModel,
                        filterBadgeCache = filterBadgeCache
                    )
                }
            }
        }
    }
}