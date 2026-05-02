package edu.dyds.movies.data

import edu.dyds.movies.data.external.RemoteMoviesDataSource
import edu.dyds.movies.data.local.LocalMoviesCache
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class MoviesRepositoryImpl(
    private val remoteMoviesDataSource: RemoteMoviesDataSource,
    private val localMoviesCache: LocalMoviesCache
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        return localMoviesCache.getCachedMovies() ?: run {
            val fetched = remoteMoviesDataSource.getPopularMovies().map { it.toDomainMovie() }
            localMoviesCache.saveMovies(fetched)
            fetched
        }
    }

    override suspend fun getMovieDetails(id: Int): Movie? {
        return remoteMoviesDataSource.getMovieDetails(id)?.toDomainMovie()
    }
}
