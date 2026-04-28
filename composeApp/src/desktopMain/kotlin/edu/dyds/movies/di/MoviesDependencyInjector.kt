package edu.dyds.movies.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.dyds.movies.data.MoviesRepositoryImpl
import edu.dyds.movies.data.external.RemoteMoviesDataSourceImpl
import edu.dyds.movies.data.local.LocalMoviesCacheImpl
import edu.dyds.movies.domain.repository.MoviesRepository
import edu.dyds.movies.domain.usecase.GetMovieDetailUseCase
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import edu.dyds.movies.domain.usecase.GetMovieDetailUseCaseImpl
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCaseImpl
import edu.dyds.movies.presentation.home.HomeViewModel
import edu.dyds.movies.presentation.detail.DetailViewModel
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private const val API_KEY = "d18da1b5da16397619c688b0263cd281"

object MoviesDependencyInjector {

    private val tmdbHttpClient =
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(DefaultRequest) {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.themoviedb.org"
                    parameters.append("api_key", API_KEY)
                }
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
        }

    private val remoteMoviesDataSource = RemoteMoviesDataSourceImpl(tmdbHttpClient)

    private val localMoviesCache = LocalMoviesCacheImpl()

    private val moviesRepository: MoviesRepository = MoviesRepositoryImpl(remoteMoviesDataSource, localMoviesCache)

    private val getPopularMoviesUseCase = GetPopularMoviesUseCaseImpl(moviesRepository)

    private val getMovieDetailUseCase = GetMovieDetailUseCaseImpl(moviesRepository)

    @Composable
    fun getHomeViewModel(): HomeViewModel {
        return viewModel { HomeViewModel(getPopularMoviesUseCase) }
    }

    @Composable
    fun getDetailViewModel(): DetailViewModel {
        return viewModel { DetailViewModel(getMovieDetailUseCase) }
    }
}