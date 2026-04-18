package edu.dyds.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

private const val MIN_VOTE_AVERAGE = 6.0

class MoviesViewModel(
    private val tmdbHttpClient: HttpClient,
) : ViewModel() {

    private val cacheMovies: MutableList<RemoteMovie> = mutableListOf()

    private val moviesStateMutableStateFlow = MutableStateFlow(MoviesUiState())

    private val movieDetailStateMutableStateFlow = MutableStateFlow(MovieDetailUiState())

    val moviesStateFlow: Flow<MoviesUiState> = moviesStateMutableStateFlow

    val movieDetailStateFlow: Flow<MovieDetailUiState> = movieDetailStateMutableStateFlow

    fun getAllMovies() {
        viewModelScope.launch {
            moviesStateMutableStateFlow.emit(
                MoviesUiState(isLoading = true)
            )
            moviesStateMutableStateFlow.emit(
                MoviesUiState(
                    isLoading = false,
                    movies = getPopularMovies().sortAndMap()
                )
            )
        }
    }

    fun getMovieDetail(id: Int) {
        viewModelScope.launch {
            movieDetailStateMutableStateFlow.emit(
                MovieDetailUiState(isLoading = true)
            )
            movieDetailStateMutableStateFlow.emit(
                MovieDetailUiState(
                    isLoading = false,
                    movie = getMovieDetails(id)?.toDomainMovie()
                )
            )
        }
    }

    private suspend fun getPopularMovies() =
        if (cacheMovies.isNotEmpty()) {
            cacheMovies
        } else {
            try {
                getTMDBPopularMovies().results.apply {
                    cacheMovies.clear()
                    cacheMovies.addAll(this)
                }
            } catch (e: Exception) {
                emptyList()
            }
        }

    private fun List<RemoteMovie>.sortAndMap(): List<QualifiedMovie> {
        return this
            .sortedByDescending { it.voteAverage }
            .map {
                QualifiedMovie(
                    movie = it.toDomainMovie(),
                    isGoodMovie = it.voteAverage >= MIN_VOTE_AVERAGE
                )
            }
    }

    private suspend fun getMovieDetails(id: Int) =
        try {
            getTMDBMovieDetails(id)
        } catch (e: Exception) {
            null
        }

    private suspend fun getTMDBMovieDetails(id: Int): RemoteMovie =
        tmdbHttpClient.get("/3/movie/$id").body()


    private suspend fun getTMDBPopularMovies(): RemoteResult =
        tmdbHttpClient.get("/3/discover/movie?sort_by=popularity.desc").body()

    data class MoviesUiState(
        val isLoading: Boolean = false,
        val movies: List<QualifiedMovie> = emptyList(),
    )

    data class MovieDetailUiState(
        val isLoading: Boolean = false,
        val movie: Movie? = null,
    )
}