package edu.dyds.movies.data.external

import edu.dyds.movies.data.external.tmdb.RemoteMovie
import edu.dyds.movies.domain.entity.Movie

interface MovieDetailExternalSource {

    suspend fun getMovieDetail(title: String): Movie?
}

