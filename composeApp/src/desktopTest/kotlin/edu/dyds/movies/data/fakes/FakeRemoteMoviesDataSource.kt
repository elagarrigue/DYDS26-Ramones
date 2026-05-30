package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.MoviesListExternalSource
import edu.dyds.movies.data.external.tmdb.TMDBMovie

class FakeMoviesListExternalSource(
    private val popularMovies: List<TMDBMovie> = emptyList()
) : MoviesListExternalSource {
    var getPopularMoviesCalls = 0

    override suspend fun getPopularMovies(): List<TMDBMovie> {
        getPopularMoviesCalls++
        return popularMovies
    }
}
