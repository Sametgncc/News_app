package com.example.read_app.ui.navigation


import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.read_app.ui.screens.detail.DetailScreen
import com.example.read_app.ui.screens.home.HomeScreen
import com.example.read_app.ui.screens.saved.SavedScreen

@Composable
fun AppNavGraph(
    application: Application,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                application = application,
                onOpenDetail = { id -> navController.navigate(Routes.detail(id)) },
                onOpenSaved = { navController.navigate(Routes.SAVED) }
            )
        }

        composable(Routes.SAVED) {
            SavedScreen(
                application = application,
                onOpenDetail = { id -> navController.navigate(Routes.detail(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.DETAIL}/{${Routes.DETAIL_ARG_ID}}",
            arguments = listOf(navArgument(Routes.DETAIL_ARG_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString(Routes.DETAIL_ARG_ID).orEmpty()
            DetailScreen(
                application = application,
                articleId = id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
