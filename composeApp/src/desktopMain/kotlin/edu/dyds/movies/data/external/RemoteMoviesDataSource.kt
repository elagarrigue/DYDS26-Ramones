package edu.dyds.movies.data.external

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface RemoteMoviesDataSource {
    suspend fun getPopularMovies(): List<RemoteMovie>
    suspend fun getMovieDetails(id: Int): RemoteMovie?
}

