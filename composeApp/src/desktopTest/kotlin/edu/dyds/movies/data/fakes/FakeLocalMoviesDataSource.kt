package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.local.LocalMoviesDataSource
import edu.dyds.movies.domain.entity.Movie

class FakeLocalMoviesDataSource(
    initialMovies: List<Movie>? = null
) : LocalMoviesDataSource {
    private var cached: MutableList<Movie>? = initialMovies?.toMutableList()
    val savedMovies: List<Movie>? get() = cached?.toList()
    var getCachedMoviesCalls = 0
    var saveMoviesCalls = 0

    override fun getCachedMovies(): List<Movie>? {
        getCachedMoviesCalls++
        return cached?.ifEmpty { null }
    }

    override fun saveMovies(movies: List<Movie>) {
        saveMoviesCalls++
        cached = movies.toMutableList()
    }
}
