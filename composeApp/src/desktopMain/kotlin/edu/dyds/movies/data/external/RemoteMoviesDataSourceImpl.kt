package edu.dyds.movies.data.external

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class RemoteMoviesDataSourceImpl(private val httpClient: HttpClient) : RemoteMoviesDataSource {
    override suspend fun getPopularMovies(): List<RemoteMovie> {
        return try {
            httpClient.get("/3/discover/movie?sort_by=popularity.desc").body<RemoteResult>().results
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieDetails(id: Int): RemoteMovie? {
        return try {
            httpClient.get("/3/movie/$id").body<RemoteMovie>()
        } catch (e: Exception) {
            null
        }
    }
}
