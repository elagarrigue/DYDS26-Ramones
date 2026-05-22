package edu.dyds.movies.data.external

import edu.dyds.movies.data.external.tmdb.RemoteMovie

interface MovieDetailExternalSource {

    suspend fun getMovieDetails(id: Int): RemoteMovie?
}

