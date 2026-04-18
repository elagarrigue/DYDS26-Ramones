package edu.dyds.movies.data.local

import edu.dyds.movies.data.external.RemoteMovie

interface LocalMoviesCache {
    fun getCachedMovies(): List<RemoteMovie>?
    fun saveMovies(movies: List<RemoteMovie>)
    fun clearCache()
}
