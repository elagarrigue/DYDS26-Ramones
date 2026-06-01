package edu.dyds.movies.data.external

import edu.dyds.movies.domain.entity.Movie

class MovieDetailExternalSourceBroker(
    private val tmdbSource: MovieDetailExternalSource,
    private val omdbSource: MovieDetailExternalSource
) : MovieDetailExternalSource {

    override suspend fun getMovieDetail(title: String): Movie? {
        val tmdbMovie = tmdbSource.getMovieDetail(title)
        val omdbMovie = omdbSource.getMovieDetail(title)

        return when {
            tmdbMovie != null && omdbMovie != null -> {
                combinateMovies(tmdbMovie, omdbMovie)
            }
            tmdbMovie != null -> {
                tmdbMovie.copy(overview = "TMDB: ${tmdbMovie.overview}")
            }
            omdbMovie != null -> {
                omdbMovie.copy(overview = "OMDB: ${omdbMovie.overview}")
            }
            else -> null
        }
    }

    private fun combinateMovies(tmdbMovie: Movie, omdbMovie: Movie): Movie {
        return Movie(
            id = tmdbMovie.id,
            title = tmdbMovie.title,
            overview = "${tmdbMovie.overview}\n\nOMDB: ${omdbMovie.overview}",
            releaseDate = tmdbMovie.releaseDate.takeIf { it.isNotEmpty() } ?: omdbMovie.releaseDate,
            poster = tmdbMovie.poster.takeIf { it.isNotEmpty() } ?: omdbMovie.poster,
            backdrop = tmdbMovie.backdrop ?: omdbMovie.backdrop,
            originalTitle = tmdbMovie.originalTitle,
            originalLanguage = tmdbMovie.originalLanguage,
            popularity = tmdbMovie.popularity,
            voteAverage = (tmdbMovie.voteAverage + omdbMovie.voteAverage) / 2
        )
    }
}


