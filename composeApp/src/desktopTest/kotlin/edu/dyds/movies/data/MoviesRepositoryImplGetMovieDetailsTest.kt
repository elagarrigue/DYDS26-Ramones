package edu.dyds.movies.data

import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.external.RemoteMoviesDataSource
import edu.dyds.movies.data.local.LocalMoviesCache
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MoviesRepositoryImplGetMovieDetailsTest {

    // ─── Fakes ───────────────────────────────────────────────────────────────
    private class FakeRemoteMoviesDataSource(
        private val popularMovies: List<RemoteMovie> = emptyList(),
        private val movieDetail: RemoteMovie? = null
    ) : RemoteMoviesDataSource {
        var receivedId: Int? = null
        override suspend fun getPopularMovies(): List<RemoteMovie> = popularMovies
        override suspend fun getMovieDetails(id: Int): RemoteMovie? {
            receivedId = id
            return movieDetail
        }
    }

    private class FakeLocalMoviesCache(
        initialMovies: List<Movie>? = null
    ) : LocalMoviesCache {
        private var cached: MutableList<Movie>? = initialMovies?.toMutableList()
        val savedMovies: List<Movie>? get() = cached?.toList()
        var getCachedMoviesCalls = 0
        var saveMoviesCalls = 0

        override fun getCachedMovies(): List<Movie>? {
            getCachedMoviesCalls++
            return cached?.ifEmpty { null }
        }
        override fun saveMovies(movies: List<Movie>) {
            saveMoviesCalls++
            cached = movies.toMutableList()
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
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

    // ─── Tests para getMovieDetails ───────────────────────────────────────────
    @Test
    fun `cuando remoto retorna una pelicula, mapea y retorna la pelicula`() = runTest {
        // Arrange
        val remoteMovie = makeRemoteMovie(id = 5, title = "The Matrix")
        val remote = FakeRemoteMoviesDataSource(movieDetail = remoteMovie)
        val cache = FakeLocalMoviesCache()
        val repository = MoviesRepositoryImpl(remote, cache)

        // Act
        val result = repository.getMovieDetails(5)

        // Assert
        assertEquals(5, result?.id)
        assertEquals("The Matrix", result?.title)
        assertEquals(5, remote.receivedId)
    }

    @Test
    fun `cuando remoto retorna null, retorna null`() = runTest {
        // Arrange
        val remote = FakeRemoteMoviesDataSource(movieDetail = null)
        val cache = FakeLocalMoviesCache()
        val repository = MoviesRepositoryImpl(remote, cache)

        // Act
        val result = repository.getMovieDetails(999)

        // Assert
        assertNull(result)
    }

    @Test
    fun `mapea correctamente todos los campos de RemoteMovie a Movie en getMovieDetails`() = runTest {
        // Arrange
        val remoteMovie = makeRemoteMovie(id = 7, title = "Interstellar")
        val remote = FakeRemoteMoviesDataSource(movieDetail = remoteMovie)
        val cache = FakeLocalMoviesCache()
        val repository = MoviesRepositoryImpl(remote, cache)

        // Act
        val result = repository.getMovieDetails(7)!!

        // Assert
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
        // Arrange
        val remoteMovie = makeRemoteMovie(id = 3)
        val remote = FakeRemoteMoviesDataSource(movieDetail = remoteMovie)
        val cache = FakeLocalMoviesCache()
        val repository = MoviesRepositoryImpl(remote, cache)

        // Act
        repository.getMovieDetails(3)

        // Assert
        assertNull(cache.savedMovies)
        assertEquals(0, cache.getCachedMoviesCalls)
        assertEquals(0, cache.saveMoviesCalls)
    }
}
