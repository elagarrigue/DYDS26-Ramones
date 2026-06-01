package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.domain.entity.Movie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OMDBMovie(
    @SerialName("imdbID") val imdbId: String,
    @SerialName("Title") val title: String,
    @SerialName("Plot") val overview: String,
    @SerialName("Released") val releaseDate: String,
    @SerialName("Year") val year: String,
    @SerialName("Poster") val poster: String,
    @SerialName("Language") val originalLanguage: String,
    @SerialName("Metascore") val metaScore: String,
    @SerialName("imdbRating") val imdbRating: String,
) {
    fun toDomainMovie(): Movie = Movie(
        id = imdbId.hashCode(),
        title = title,
        overview = overview,
        releaseDate = if (releaseDate.isNotEmpty() && releaseDate != "N/A") releaseDate else year,
        poster = poster,
        backdrop = null,
        originalTitle = title,
        originalLanguage = originalLanguage,
        popularity = 0.0,
        voteAverage = imdbRating.toDoubleOrNull() ?: 0.0,
    )
}