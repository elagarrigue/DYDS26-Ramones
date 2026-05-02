package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie

class LocalMoviesDataSourceImpl : LocalMoviesDataSource {
    private val cacheMovies: MutableList<Movie> = mutableListOf()

    override fun getCachedMovies(): List<Movie>? {
        return cacheMovies.ifEmpty { null }
    }

    override fun saveMovies(movies: List<Movie>) {
        cacheMovies.clear()
        cacheMovies.addAll(movies)
    }
}
