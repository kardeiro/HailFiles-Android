package com.kardeiro.hailfiles.ui.navigation

import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kardeiro.hailfiles.ui.detail.DetailScreen
import com.kardeiro.hailfiles.ui.detail.DetailViewModel
import com.kardeiro.hailfiles.ui.home.HomeScreen
import com.kardeiro.hailfiles.ui.home.HomeViewModel

@Composable
fun HailFilesNavGraph(
    navController: NavHostController
) {
    val homeViewModel: HomeViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onAppClick = { appId ->
                    navController.navigate(Routes.detail(appId))
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("appId") { type = NavType.StringType })
        ) { backStackEntry ->
            val appId = backStackEntry.arguments?.getString("appId") ?: return@composable
            val detailViewModel: DetailViewModel = viewModel()

            DetailScreen(
                appId = appId,
                viewModel = detailViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
