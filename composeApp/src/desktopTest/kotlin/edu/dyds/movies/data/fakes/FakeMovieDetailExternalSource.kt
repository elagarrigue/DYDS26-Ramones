package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.domain.entity.Movie

class FakeMovieDetailExternalSource(
    private val movieDetail: Movie? = null
) : MovieDetailExternalSource {
    var receivedTitle: String? = null

    override suspend fun getMovieDetail(title: String): Movie? {
        receivedTitle = title
        return movieDetail
    }
}

