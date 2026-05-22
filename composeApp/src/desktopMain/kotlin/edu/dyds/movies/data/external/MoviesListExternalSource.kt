package edu.dyds.movies.data.external

import edu.dyds.movies.data.external.tmdb.RemoteMovie

interface MoviesListExternalSource {
    suspend fun getPopularMovies(): List<RemoteMovie>
}