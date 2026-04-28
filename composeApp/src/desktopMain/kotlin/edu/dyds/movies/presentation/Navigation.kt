@file:Suppress("FunctionName")

package edu.dyds.movies.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.dyds.movies.di.MoviesDependencyInjector.getHomeViewModel
import edu.dyds.movies.di.MoviesDependencyInjector.getDetailViewModel
import edu.dyds.movies.presentation.home.HomeViewModel
import edu.dyds.movies.presentation.detail.DetailViewModel
import edu.dyds.movies.presentation.detail.DetailScreen
import edu.dyds.movies.presentation.home.HomeScreen

private const val HOME = "home"

private const val DETAIL = "detail"

private const val MOVIE_ID = "movieId"

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val homeViewModel = getHomeViewModel()
    val detailViewModel = getDetailViewModel()

    NavHost(navController = navController, startDestination = HOME) {
        homeDestination(navController, homeViewModel)
        detailDestination(navController, detailViewModel)
    }
}

private fun NavGraphBuilder.homeDestination(
    navController: NavHostController,
    homeViewModel: HomeViewModel
) {
    composable(HOME) {
        HomeScreen(
            viewModel = homeViewModel,
            onGoodMovieClick = {
                navController.navigate("$DETAIL/${it.id}")
            }
        )
    }
}

private fun NavGraphBuilder.detailDestination(
    navController: NavHostController,
    detailViewModel: DetailViewModel
) {
    composable(
        route = "$DETAIL/{$MOVIE_ID}",
        arguments = listOf(navArgument(MOVIE_ID) { type = NavType.IntType })
    ) { backstackEntry ->
        val movieId = backstackEntry.arguments?.getInt(MOVIE_ID)

        movieId?.let {
            DetailScreen(detailViewModel, it, onBack = { navController.popBackStack() })
        }
    }
}
