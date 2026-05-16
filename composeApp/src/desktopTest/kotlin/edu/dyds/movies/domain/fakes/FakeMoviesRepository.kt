package edu.dyds.movies.domain.fakes

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class FakeMoviesRepository(
    private val popularMovies: List<Movie> = emptyList(),
    private val movieDetail: Movie? = null
) : MoviesRepository {
    override suspend fun getPopularMovies(): List<Movie> = popularMovies
    override suspend fun getMovieDetails(id: Int): Movie? = movieDetail
}

