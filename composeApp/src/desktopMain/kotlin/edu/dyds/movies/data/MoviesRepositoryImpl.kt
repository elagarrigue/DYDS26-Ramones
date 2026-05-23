package edu.dyds.movies.data

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.data.external.MoviesListExternalSource
import edu.dyds.movies.data.local.LocalMoviesDataSource
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class MoviesRepositoryImpl(
    private val moviesListExternalSource: MoviesListExternalSource,
    private val movieDetailExternalSource: MovieDetailExternalSource,
    private val localMoviesDataSource: LocalMoviesDataSource
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        return localMoviesDataSource.getCachedMovies() ?: run {
            val fetched = moviesListExternalSource.getPopularMovies().map { it.toDomainMovie() }
            localMoviesDataSource.saveMovies(fetched)
            fetched
        }
    }

    override suspend fun getMovieDetail(title: String): Movie? {
        return movieDetailExternalSource.getMovieDetail(title)
    }
}
