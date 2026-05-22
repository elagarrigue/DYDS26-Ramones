package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.data.external.tmdb.RemoteMovie

class FakeMovieDetailExternalSource(
    private val movieDetail: RemoteMovie? = null
) : MovieDetailExternalSource {
    var receivedId: Int? = null

    override suspend fun getMovieDetails(id: Int): RemoteMovie? {
        receivedId = id
        return movieDetail
    }
}

