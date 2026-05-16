package edu.dyds.movies.data

import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.fakes.FakeLocalMoviesDataSource
import edu.dyds.movies.data.fakes.FakeRemoteMoviesDataSource
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MoviesRepositoryImplGetMovieDetailsTest {

    private fun makeRemoteMovie(
        id: Int = 1,
        title: String = "Movie $id"
    ) = RemoteMovie(
        id = id,
        title = title,
        overview = "Overview $id",
        releaseDate = "2024-01-01",
        posterPath = "/poster$id.jpg",
        backdropPath = "/backdrop$id.jpg",
        originalTitle = title,
        originalLanguage = "en",
        popularity = 100.0,
        voteAverage = 7.5
    )

    @Test
    fun `cuando remoto retorna una pelicula, mapea y retorna la pelicula`() = runTest {
        val remoteMovie = makeRemoteMovie(id = 5, title = "The Matrix")
        val remote = FakeRemoteMoviesDataSource(movieDetail = remoteMovie)
        val cache = FakeLocalMoviesDataSource()
        val repository = MoviesRepositoryImpl(remote, cache)

        val result = repository.getMovieDetails(5)

        assertEquals(5, result?.id)
        assertEquals("The Matrix", result?.title)
        assertEquals(5, remote.receivedId)
    }

    @Test
    fun `cuando remoto retorna null, retorna null`() = runTest {
        val remote = FakeRemoteMoviesDataSource(movieDetail = null)
        val cache = FakeLocalMoviesDataSource()
        val repository = MoviesRepositoryImpl(remote, cache)

        val result = repository.getMovieDetails(999)

        assertNull(result)
    }

    @Test
    fun `mapea correctamente todos los campos de RemoteMovie a Movie en getMovieDetails`() = runTest {
        val remoteMovie = makeRemoteMovie(id = 7, title = "Interstellar")
        val remote = FakeRemoteMoviesDataSource(movieDetail = remoteMovie)
        val cache = FakeLocalMoviesDataSource()
        val repository = MoviesRepositoryImpl(remote, cache)

        val result = repository.getMovieDetails(7)!!

        assertEquals(7, result.id)
        assertEquals("Interstellar", result.title)
        assertEquals("Overview 7", result.overview)
        assertEquals("2024-01-01", result.releaseDate)
        assertEquals("https://image.tmdb.org/t/p/w185/poster7.jpg", result.poster)
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop7.jpg", result.backdrop)
        assertEquals("Interstellar", result.originalTitle)
        assertEquals("en", result.originalLanguage)
        assertEquals(100.0, result.popularity)
        assertEquals(7.5, result.voteAverage)
    }

    @Test
    fun `no interactua con el cache local en getMovieDetails`() = runTest {
        val remoteMovie = makeRemoteMovie(id = 3)
        val remote = FakeRemoteMoviesDataSource(movieDetail = remoteMovie)
        val cache = FakeLocalMoviesDataSource()
        val repository = MoviesRepositoryImpl(remote, cache)

        repository.getMovieDetails(3)

        assertNull(cache.savedMovies)
        assertEquals(0, cache.getCachedMoviesCalls)
        assertEquals(0, cache.saveMoviesCalls)
    }
}
