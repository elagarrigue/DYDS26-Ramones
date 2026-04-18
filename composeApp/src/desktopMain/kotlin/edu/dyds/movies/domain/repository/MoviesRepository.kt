package edu.dyds.movies.domain.repository

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie

interface MoviesRepository {
    suspend fun getPopularMovies(): List<QualifiedMovie>
    suspend fun getMovieDetails(id: Int): Movie?
}
