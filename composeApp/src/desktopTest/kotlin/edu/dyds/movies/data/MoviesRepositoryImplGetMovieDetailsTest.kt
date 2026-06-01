package edu.dyds.movies.data

import edu.dyds.movies.data.fakes.FakeLocalMoviesDataSource
import edu.dyds.movies.data.fakes.FakeMovieDetailExternalSource
import edu.dyds.movies.data.fakes.FakeMoviesListExternalSource
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MoviesRepositoryImplGetMovieDetailsTest {

    private fun makeMovie(
        id: Int = 1,
        title: String = "Movie $id"
    ) = Movie(
        id = id,
        title = title,
        overview = "Overview $id",
        releaseDate = "2024-01-01",
        poster = "https://image.tmdb.org/t/p/w185/poster$id.jpg",
        backdrop = "https://image.tmdb.org/t/p/w780/backdrop$id.jpg",
        originalTitle = title,
        originalLanguage = "en",
        popularity = 100.0,
        voteAverage = 7.5
    )

    @Test
    fun `cuando remoto retorna una pelicula, mapea y retorna la pelicula`() = runTest {
        val movie = makeMovie(id = 5, title = "The Matrix")
        val detailSource = FakeMovieDetailExternalSource(movieDetail = movie)
        val repository = MoviesRepositoryImpl(
            moviesListExternalSource = FakeMoviesListExternalSource(),
            movieDetailExternalSource = detailSource,
            localMoviesDataSource = FakeLocalMoviesDataSource()
        )

        val result = repository.getMovieDetail("The Matrix")

        assertEquals(5, result?.id)
        assertEquals("The Matrix", result?.title)
        assertEquals("The Matrix", detailSource.receivedTitle)
    }

    @Test
    fun `cuando remoto retorna null, retorna null`() = runTest {
        val repository = MoviesRepositoryImpl(
            moviesListExternalSource = FakeMoviesListExternalSource(),
            movieDetailExternalSource = FakeMovieDetailExternalSource(movieDetail = null),
            localMoviesDataSource = FakeLocalMoviesDataSource()
        )

        val result = repository.getMovieDetail("Unknown Movie")

        assertNull(result)
    }

    @Test
    fun `devuelve correctamente la pelicula obtenida del datasource`() = runTest {
        val movie = makeMovie(id = 7, title = "Interstellar")
        val repository = MoviesRepositoryImpl(
            moviesListExternalSource = FakeMoviesListExternalSource(),
            movieDetailExternalSource = FakeMovieDetailExternalSource(movieDetail = movie),
            localMoviesDataSource = FakeLocalMoviesDataSource()
        )

        val result = repository.getMovieDetail("Interstellar")!!

        assertEquals(movie, result)
    }

    @Test
    fun `no interactua con el cache local en getMovieDetails`() = runTest {
        val cache = FakeLocalMoviesDataSource()
        val repository = MoviesRepositoryImpl(
            moviesListExternalSource = FakeMoviesListExternalSource(),
            movieDetailExternalSource = FakeMovieDetailExternalSource(movieDetail = makeMovie(3)),
            localMoviesDataSource = cache
        )

        repository.getMovieDetail("Movie 3")

        assertNull(cache.savedMovies)
        assertEquals(0, cache.getCachedMoviesCalls)
        assertEquals(0, cache.saveMoviesCalls)
    }
}
