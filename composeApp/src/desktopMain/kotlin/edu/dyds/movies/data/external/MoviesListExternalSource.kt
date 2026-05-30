package edu.dyds.movies.data.external

import edu.dyds.movies.data.external.tmdb.TMDBMovie

interface MoviesListExternalSource {
    suspend fun getPopularMovies(): List<TMDBMovie>
}