package edu.dyds.movies.data

import edu.dyds.movies.data.external.RemoteMoviesDataSource
import edu.dyds.movies.data.local.LocalMoviesDataSource
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class MoviesRepositoryImpl(
    private val remoteMoviesDataSource: RemoteMoviesDataSource,
    private val localMoviesDataSource: LocalMoviesDataSource
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        return localMoviesDataSource.getCachedMovies() ?: run {
            val fetched = remoteMoviesDataSource.getPopularMovies().map { it.toDomainMovie() }
            localMoviesDataSource.saveMovies(fetched)
            fetched
        }
    }

    override suspend fun getMovieDetails(id: Int): Movie? {
        return remoteMoviesDataSource.getMovieDetails(id)?.toDomainMovie()
    }
}
