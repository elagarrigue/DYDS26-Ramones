package edu.dyds.movies.data

import edu.dyds.movies.data.external.tmdb.RemoteMovie
import edu.dyds.movies.data.fakes.FakeLocalMoviesDataSource
import edu.dyds.movies.data.fakes.FakeMoviesListExternalSource
import edu.dyds.movies.data.fakes.FakeMovieDetailExternalSource
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MoviesRepositoryImplGetPopularMoviesTest {
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

    private fun makeRepository(
        remoteMovies: List<RemoteMovie> = emptyList(),
        initialCache: List<Movie>? = null
    ) = MoviesRepositoryImpl(
        moviesListExternalSource = FakeMoviesListExternalSource(popularMovies = remoteMovies),
        movieDetailExternalSource = FakeMovieDetailExternalSource(),
        localMoviesDataSource = FakeLocalMoviesDataSource(initialMovies = initialCache)
    )

    @Test
    fun `cuando el cache esta vacio, obtiene de remoto y retorna peliculas`() = runTest {
        val repository = makeRepository(remoteMovies = listOf(makeRemoteMovie(1), makeRemoteMovie(2)))

        val result = repository.getPopularMovies()

        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals(2, result[1].id)
    }

    @Test
    fun `cuando el cache esta vacio, guarda las peliculas obtenidas en el cache`() = runTest {
        val cache = FakeLocalMoviesDataSource(initialMovies = null)
        val repository = MoviesRepositoryImpl(
            moviesListExternalSource = FakeMoviesListExternalSource(popularMovies = listOf(makeRemoteMovie(1), makeRemoteMovie(2))),
            movieDetailExternalSource = FakeMovieDetailExternalSource(),
            localMoviesDataSource = cache
        )

        repository.getPopularMovies()

        assertEquals(2, cache.savedMovies?.size)
        assertEquals(1, cache.savedMovies?.get(0)?.id)
    }

    @Test
    fun `cuando el cache tiene peliculas, retorna las del cache sin consultar remoto`() = runTest {
        val listSource = FakeMoviesListExternalSource(popularMovies = listOf(makeRemoteMovie(99)))
        val repository = MoviesRepositoryImpl(
            moviesListExternalSource = listSource,
            movieDetailExternalSource = FakeMovieDetailExternalSource(),
            localMoviesDataSource = FakeLocalMoviesDataSource(initialMovies = listOf(makeMovie(10), makeMovie(20)))
        )

        val result = repository.getPopularMovies()

        assertEquals(2, result.size)
        assertEquals(10, result[0].id)
        assertEquals(20, result[1].id)
        assertEquals(0, listSource.getPopularMoviesCalls)
    }

    @Test
    fun `mapea correctamente los campos de RemoteMovie a Movie en getPopularMovies`() = runTest {
        val repository = makeRepository(remoteMovies = listOf(makeRemoteMovie(id = 42, title = "Inception")))

        val movie = repository.getPopularMovies().first()

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
        val cache = FakeLocalMoviesDataSource()
        val repository = MoviesRepositoryImpl(
            moviesListExternalSource = FakeMoviesListExternalSource(popularMovies = emptyList()),
            movieDetailExternalSource = FakeMovieDetailExternalSource(),
            localMoviesDataSource = cache
        )

        val result = repository.getPopularMovies()

        assertEquals(0, result.size)
        assertEquals(0, cache.savedMovies?.size)
    }
}
