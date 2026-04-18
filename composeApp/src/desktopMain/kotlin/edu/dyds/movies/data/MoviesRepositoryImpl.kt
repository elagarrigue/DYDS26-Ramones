// Placeholder for MoviesRepositoryImpl.kt
package edu.dyds.movies.data

import edu.dyds.movies.data.external.RemoteMoviesDataSource
import edu.dyds.movies.data.local.LocalMoviesCache
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.repository.MoviesRepository

private const val MIN_VOTE_AVERAGE = 6.0

class MoviesRepositoryImpl(
    private val remoteMoviesDataSource: RemoteMoviesDataSource,
    private val localMoviesCache: LocalMoviesCache
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<QualifiedMovie> {
        val remoteMovies = localMoviesCache.getCachedMovies() ?: run {
            val fetched = remoteMoviesDataSource.getPopularMovies()
            localMoviesCache.saveMovies(fetched)
            fetched
        }
        return remoteMovies.sortAndMap()
    }

    override suspend fun getMovieDetails(id: Int): Movie? {
        return remoteMoviesDataSource.getMovieDetails(id)?.toDomainMovie()
    }

    private fun List<edu.dyds.movies.data.external.RemoteMovie>.sortAndMap(): List<QualifiedMovie> {
        return this
            .sortedByDescending { it.voteAverage }
            .map {
                QualifiedMovie(
                    movie = it.toDomainMovie(),
                    isGoodMovie = it.voteAverage >= MIN_VOTE_AVERAGE
                )
            }
    }
}
