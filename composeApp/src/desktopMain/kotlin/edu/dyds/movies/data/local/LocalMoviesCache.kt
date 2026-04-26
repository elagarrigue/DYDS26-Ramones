package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie

interface LocalMoviesCache {
    fun getCachedMovies(): List<Movie>?
    fun saveMovies(movies: List<Movie>)
}
