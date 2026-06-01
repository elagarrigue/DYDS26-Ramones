package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.domain.entity.Movie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class OMDBMoviesExternalSource(private val httpClient: HttpClient) : MovieDetailExternalSource {
    override suspend fun getMovieDetail(title: String): Movie? {
        return try {
            httpClient.get("/?t=${title.replace(" ", "+")}").body<OMDBMovie>().toDomainMovie()
        } catch (e: Exception) {
            null
        }
    }
}