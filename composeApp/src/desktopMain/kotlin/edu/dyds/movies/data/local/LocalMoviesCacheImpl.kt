package edu.dyds.movies.data.local

import edu.dyds.movies.data.external.RemoteMovie

class LocalMoviesCacheImpl : LocalMoviesCache {
    private val cacheMovies: MutableList<RemoteMovie> = mutableListOf()

    override fun getCachedMovies(): List<RemoteMovie>? {
        return if (cacheMovies.isNotEmpty()) cacheMovies else null
    }

    override fun saveMovies(movies: List<RemoteMovie>) {
        cacheMovies.clear()
        cacheMovies.addAll(movies)
    }

    override fun clearCache() {
        cacheMovies.clear()
    }
}
