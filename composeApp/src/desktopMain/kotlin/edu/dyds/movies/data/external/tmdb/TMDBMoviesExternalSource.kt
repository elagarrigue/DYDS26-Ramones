package edu.dyds.movies.data.external.tmdb

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.data.external.MoviesListExternalSource
import edu.dyds.movies.domain.entity.Movie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class TMDBMoviesExternalSource(private val httpClient: HttpClient) : MoviesListExternalSource,
    MovieDetailExternalSource {
    override suspend fun getPopularMovies(): List<RemoteMovie> {
        return try {
            httpClient.get("/3/discover/movie?sort_by=popularity.desc").body<RemoteResult>().results
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieDetail(title: String): Movie? {
        return try {
            val results = httpClient.get("/3/search/movie?query=${title.replace(" ", "+")}").body<RemoteResult>().results
            results.firstOrNull()?.toDomainMovie()
        } catch (e: Exception) {
            null
        }
    }
}
