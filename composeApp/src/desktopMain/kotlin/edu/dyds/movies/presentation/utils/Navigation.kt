@file:Suppress("FunctionName")

package edu.dyds.movies

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.dyds.movies.MoviesDependencyInjector.getMoviesViewModel

private const val HOME = "home"

private const val DETAIL = "detail"

private const val MOVIE_ID = "movieId"

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HOME) {
        homeDestination(navController)
        detailDestination(navController)
    }
}

private fun NavGraphBuilder.homeDestination(navController: NavHostController) {
    composable(HOME) {
        HomeScreen(
            viewModel = getMoviesViewModel(),
            onGoodMovieClick = {
                navController.navigate("$DETAIL/${it.id}")
            }
        )
    }
}

private fun NavGraphBuilder.detailDestination(navController: NavHostController) {
    composable(
        route = "$DETAIL/{$MOVIE_ID}",
        arguments = listOf(navArgument(MOVIE_ID) { type = NavType.IntType })
    ) { backstackEntry ->
        val movieId = backstackEntry.arguments?.getInt(MOVIE_ID)

        movieId?.let {
            DetailScreen(getMoviesViewModel(), it, onBack = { navController.popBackStack() })
        }
    }
}
