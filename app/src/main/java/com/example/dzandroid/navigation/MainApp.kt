package com.example.dzandroid.navigation

import com.example.dzandroid.presentation.screens.RepoDetailsScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dzandroid.core.FilterBadgeCache
import com.example.dzandroid.presentation.RepoViewModel
import com.example.dzandroid.presentation.screens.MainScreen
import com.example.dzandroid.profile.presentation.ProfileViewModel
import com.example.dzandroid.profile.presentation.screens.EditProfileScreen
import com.example.dzandroid.profile.presentation.screens.ProfileScreen


@Composable
fun MainApp(
    navController: NavHostController,
    viewModel: RepoViewModel,
    profileViewModel: ProfileViewModel,
    filterBadgeCache: FilterBadgeCache
) {
    var selectedTab by remember { mutableStateOf(0) }

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = viewModel,
                profileViewModel = profileViewModel,
                navController = navController,
                onRepoClick = { repo ->
                    viewModel.selectRepository(repo)
                    navController.navigate(Screen.RepoDetails.route)
                },
                selectedTab = selectedTab,
                onTabSelected = { tabIndex ->
                    selectedTab = tabIndex
                },
                filterBadgeCache = filterBadgeCache
            )
        }

        composable(Screen.RepoDetails.route) {
            val selectedRepo = viewModel.selectedRepository.collectAsState().value

            LaunchedEffect(selectedRepo) {
                if (selectedRepo == null) {
                    navController.popBackStack()
                }
            }

            if (selectedRepo != null) {
                RepoDetailsScreen(
                    viewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = profileViewModel,
                navController = navController,
                onEditClick = {
                    navController.navigate(Screen.EditProfile.route)
                }
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                viewModel = profileViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}