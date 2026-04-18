@file:Suppress("FunctionName")

package edu.dyds.movies.presentation.utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.dyds.movies.di.MoviesDependencyInjector.getMoviesViewModel
import edu.dyds.movies.presentation.MoviesViewModel
import edu.dyds.movies.presentation.detail.DetailScreen
import edu.dyds.movies.presentation.home.HomeScreen

private const val HOME = "home"

private const val DETAIL = "detail"

private const val MOVIE_ID = "movieId"

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val moviesViewModel = getMoviesViewModel()

    NavHost(navController = navController, startDestination = HOME) {
        homeDestination(navController, moviesViewModel)
        detailDestination(navController, moviesViewModel)
    }
}

private fun NavGraphBuilder.homeDestination(
    navController: NavHostController,
    moviesViewModel: MoviesViewModel
) {
    composable(HOME) {
        HomeScreen(
            viewModel = moviesViewModel,
            onGoodMovieClick = {
                navController.navigate("$DETAIL/${it.id}")
            }
        )
    }
}

private fun NavGraphBuilder.detailDestination(
    navController: NavHostController,
    moviesViewModel: MoviesViewModel
) {
    composable(
        route = "$DETAIL/{$MOVIE_ID}",
        arguments = listOf(navArgument(MOVIE_ID) { type = NavType.IntType })
    ) { backstackEntry ->
        val movieId = backstackEntry.arguments?.getInt(MOVIE_ID)

        movieId?.let {
            DetailScreen(moviesViewModel, it, onBack = { navController.popBackStack() })
        }
    }
}
