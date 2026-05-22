package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.MoviesListExternalSource
import edu.dyds.movies.data.external.tmdb.RemoteMovie

class FakeMoviesListExternalSource(
    private val popularMovies: List<RemoteMovie> = emptyList()
) : MoviesListExternalSource {
    var getPopularMoviesCalls = 0

    override suspend fun getPopularMovies(): List<RemoteMovie> {
        getPopularMoviesCalls++
        return popularMovies
    }
}
