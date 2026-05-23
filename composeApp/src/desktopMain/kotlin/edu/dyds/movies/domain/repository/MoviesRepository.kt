package edu.dyds.movies.domain.repository

import edu.dyds.movies.domain.entity.Movie

interface MoviesRepository {
    suspend fun getPopularMovies(): List<Movie>
    suspend fun getMovieDetail(title: String): Movie?
}
