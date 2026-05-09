package edu.dyds.movies.data

import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.external.RemoteMoviesDataSource
import edu.dyds.movies.data.local.LocalMoviesCache
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MoviesRepositoryImplGetPopularMoviesTest {

    // ─── Fakes ───────────────────────────────────────────────────────────────
    private class FakeRemoteMoviesDataSource(
        private val popularMovies: List<RemoteMovie> = emptyList(),
        private val movieDetail: RemoteMovie? = null
    ) : RemoteMoviesDataSource {
        var getPopularMoviesCalls = 0
        override suspend fun getPopularMovies(): List<RemoteMovie> {
            getPopularMoviesCalls++
            return popularMovies
        }
        override suspend fun getMovieDetails(id: Int): RemoteMovie? = movieDetail
    }

    private class FakeLocalMoviesCache(
        initialMovies: List<Movie>? = null
    ) : LocalMoviesCache {
        private var cached: MutableList<Movie>? = initialMovies?.toMutableList()
        val savedMovies: List<Movie>? get() = cached?.toList()

        override fun getCachedMovies(): List<Movie>? = cached?.ifEmpty { null }
        override fun saveMovies(movies: List<Movie>) {
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

    // ─── Tests para getPopularMovies ──────────────────────────────────────────
    @Test
    fun `cuando el cache esta vacio, obtiene de remoto y retorna peliculas`() = runTest {
        // Arrange
        val remoteMovies = listOf(makeRemoteMovie(1), makeRemoteMovie(2))
        val remote = FakeRemoteMoviesDataSource(popularMovies = remoteMovies)
        val cache = FakeLocalMoviesCache(initialMovies = null)
        val repository = MoviesRepositoryImpl(remote, cache)

        // Act
        val result = repository.getPopularMovies()

        // Assert
        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals(2, result[1].id)
    }

    @Test
    fun `cuando el cache esta vacio, guarda las peliculas obtenidas en el cache`() = runTest {
        // Arrange
        val remoteMovies = listOf(makeRemoteMovie(1), makeRemoteMovie(2))
        val remote = FakeRemoteMoviesDataSource(popularMovies = remoteMovies)
        val cache = FakeLocalMoviesCache(initialMovies = null)
        val repository = MoviesRepositoryImpl(remote, cache)

        // Act
        repository.getPopularMovies()

        // Assert
        assertEquals(2, cache.savedMovies?.size)
        assertEquals(1, cache.savedMovies?.get(0)?.id)
    }

    @Test
    fun `cuando el cache tiene peliculas, retorna las del cache sin consultar remoto`() = runTest {
        // Arrange
        val cachedMovies = listOf(makeMovie(10), makeMovie(20))
        val remote = FakeRemoteMoviesDataSource(popularMovies = listOf(makeRemoteMovie(99)))
        val cache = FakeLocalMoviesCache(initialMovies = cachedMovies)
        val repository = MoviesRepositoryImpl(remote, cache)

        // Act
        val result = repository.getPopularMovies()

        // Assert
        assertEquals(2, result.size)
        assertEquals(10, result[0].id)
        assertEquals(20, result[1].id)
        assertEquals(0, remote.getPopularMoviesCalls)
    }

    @Test
    fun `mapea correctamente los campos de RemoteMovie a Movie en getPopularMovies`() = runTest {
        // Arrange
        val remote = FakeRemoteMoviesDataSource(
            popularMovies = listOf(makeRemoteMovie(id = 42, title = "Inception"))
        )
        val cache = FakeLocalMoviesCache()
        val repository = MoviesRepositoryImpl(remote, cache)

        // Act
        val result = repository.getPopularMovies()
        val movie = result.first()

        // Assert
        assertEquals(42, movie.id)
        assertEquals("Inception", movie.title)
        assertEquals("https://image.tmdb.org/t/p/w185/poster42.jpg", movie.poster)
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop42.jpg", movie.backdrop)
        assertEquals("Overview 42", movie.overview)
        assertEquals("2024-01-01", movie.releaseDate)
        assertEquals("Inception", movie.originalTitle)
        assertEquals("en", movie.originalLanguage)
        assertEquals(100.0, movie.popularity)
        assertEquals(7.5, movie.voteAverage)
    }

    @Test
    fun `cuando remoto retorna lista vacia, retorna vacio y guarda vacio en cache`() = runTest {
        // Arrange
        val remote = FakeRemoteMoviesDataSource(popularMovies = emptyList())
        val cache = FakeLocalMoviesCache()
        val repository = MoviesRepositoryImpl(remote, cache)

        // Act
        val result = repository.getPopularMovies()

        // Assert
        assertEquals(0, result.size)
        assertEquals(0, cache.savedMovies?.size)
    }
}
