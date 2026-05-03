package edu.dyds.movies.data.external

interface RemoteMoviesDataSource {
    suspend fun getPopularMovies(): List<RemoteMovie>
    suspend fun getMovieDetails(id: Int): RemoteMovie?
}

