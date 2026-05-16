package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.external.RemoteMoviesDataSource

class FakeRemoteMoviesDataSource(
    private val popularMovies: List<RemoteMovie> = emptyList(),
    private val movieDetail: RemoteMovie? = null
) : RemoteMoviesDataSource {
    var getPopularMoviesCalls = 0
    var receivedId: Int? = null

    override suspend fun getPopularMovies(): List<RemoteMovie> {
        getPopularMoviesCalls++
        return popularMovies
    }

    override suspend fun getMovieDetails(id: Int): RemoteMovie? {
        receivedId = id
        return movieDetail
    }
}
