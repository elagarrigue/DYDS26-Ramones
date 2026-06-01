package edu.dyds.movies.data.external

import edu.dyds.movies.data.fakes.FakeMovieDetailExternalSource
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MovieDetailExternalSourceBrokerTest {

    private fun createTestMovie(
        id: Int = 1,
        title: String = "Test Movie",
        overview: String = "Test Overview",
        releaseDate: String = "2024-01-01",
        poster: String = "http://test.com/poster.jpg",
        backdrop: String? = "http://test.com/backdrop.jpg",
        originalTitle: String = "Original Title",
        originalLanguage: String = "en",
        popularity: Double = 10.0,
        voteAverage: Double = 8.0
    ) = Movie(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        poster = poster,
        backdrop = backdrop,
        originalTitle = originalTitle,
        originalLanguage = originalLanguage,
        popularity = popularity,
        voteAverage = voteAverage
    )

    @Test
    fun `obtenerDetallePelícula debe retornar película combinada cuando tanto TMDB como OMDB retornan resultados`() = runTest {
        val tmdbMovie = createTestMovie(
            id = 1,
            title = "The Matrix",
            overview = "A computer hacker"
        )
        val omdbMovie = createTestMovie(
            id = 2,
            title = "The Matrix",
            overview = "A hacker discovers reality"
        )
        val tmdbSource = FakeMovieDetailExternalSource(tmdbMovie)
        val omdbSource = FakeMovieDetailExternalSource(omdbMovie)
        val broker = MovieDetailExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieDetail("The Matrix")

        assertEquals(tmdbMovie.id, result?.id)
        assertEquals(tmdbMovie.title, result?.title)
        assertEquals("A computer hacker\n\nOMDB: A hacker discovers reality", result?.overview)
        assertEquals(tmdbMovie.originalTitle, result?.originalTitle)
        assertEquals((8.0 + 8.0) / 2, result?.voteAverage)
    }

    @Test
    fun `obtenerDetallePelícula debe retornar película de TMDB con descripción prefijada cuando solo TMDB retorna resultado`() = runTest {
        val tmdbMovie = createTestMovie(
            id = 1,
            title = "Inception",
            overview = "A thief who steals corporate secrets"
        )
        val tmdbSource = FakeMovieDetailExternalSource(tmdbMovie)
        val omdbSource = FakeMovieDetailExternalSource(null)
        val broker = MovieDetailExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieDetail("Inception")

        assertEquals(tmdbMovie.id, result?.id)
        assertEquals(tmdbMovie.title, result?.title)
        assertEquals("TMDB: A thief who steals corporate secrets", result?.overview)
    }

    @Test
    fun `obtenerDetallePelícula debe retornar película de OMDB con descripción prefijada cuando solo OMDB retorna resultado`() = runTest {
        val omdbMovie = createTestMovie(
            id = 2,
            title = "Interstellar",
            overview = "A team of astronauts"
        )
        val tmdbSource = FakeMovieDetailExternalSource(null)
        val omdbSource = FakeMovieDetailExternalSource(omdbMovie)
        val broker = MovieDetailExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieDetail("Interstellar")

        assertEquals(omdbMovie.id, result?.id)
        assertEquals(omdbMovie.title, result?.title)
        assertEquals("OMDB: A team of astronauts", result?.overview)
    }

    @Test
    fun `obtenerDetallePelícula debe retornar nulo cuando ni TMDB ni OMDB retornan resultado`() = runTest {
        val tmdbSource = FakeMovieDetailExternalSource(null)
        val omdbSource = FakeMovieDetailExternalSource(null)
        val broker = MovieDetailExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieDetail("NonexistentMovie")

        assertNull(result)
    }

    @Test
    fun `obtenerDetallePelícula debe usar póster de TMDB cuando ambos tienen rutas de póster y TMDB no está vacío`() = runTest {
        val tmdbMovie = createTestMovie(
            poster = "https://images.tmdb.org/poster.jpg"
        )
        val omdbMovie = createTestMovie(
            poster = "https://images.omdb.org/poster.jpg"
        )
        val tmdbSource = FakeMovieDetailExternalSource(tmdbMovie)
        val omdbSource = FakeMovieDetailExternalSource(omdbMovie)
        val broker = MovieDetailExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieDetail("TestMovie")

        assertEquals("https://images.tmdb.org/poster.jpg", result?.poster)
    }

    @Test
    fun `obtenerDetallePelícula debe usar póster de OMDB cuando el póster de TMDB está vacío`() = runTest {
        val tmdbMovie = createTestMovie(
            poster = ""
        )
        val omdbMovie = createTestMovie(
            poster = "https://images.omdb.org/poster.jpg"
        )
        val tmdbSource = FakeMovieDetailExternalSource(tmdbMovie)
        val omdbSource = FakeMovieDetailExternalSource(omdbMovie)
        val broker = MovieDetailExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieDetail("TestMovie")

        assertEquals("https://images.omdb.org/poster.jpg", result?.poster)
    }

    @Test
    fun `obtenerDetallePelícula debe usar telón de TMDB cuando ambos tienen telones`() = runTest {
        val tmdbBackdrop = "https://images.tmdb.org/backdrop.jpg"
        val omdbBackdrop = "https://images.omdb.org/backdrop.jpg"
        val tmdbMovie = createTestMovie(backdrop = tmdbBackdrop)
        val omdbMovie = createTestMovie(backdrop = omdbBackdrop)
        val tmdbSource = FakeMovieDetailExternalSource(tmdbMovie)
        val omdbSource = FakeMovieDetailExternalSource(omdbMovie)
        val broker = MovieDetailExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieDetail("TestMovie")

        assertEquals(tmdbBackdrop, result?.backdrop)
    }

    @Test
    fun `obtenerDetallePelícula debe usar telón de OMDB cuando el telón de TMDB es nulo`() = runTest {
        val omdbBackdrop = "https://images.omdb.org/backdrop.jpg"
        val tmdbMovie = createTestMovie(backdrop = null)
        val omdbMovie = createTestMovie(backdrop = omdbBackdrop)
        val tmdbSource = FakeMovieDetailExternalSource(tmdbMovie)
        val omdbSource = FakeMovieDetailExternalSource(omdbMovie)
        val broker = MovieDetailExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieDetail("TestMovie")

        assertEquals(omdbBackdrop, result?.backdrop)
    }

    @Test
    fun `obtenerDetallePelícula debe promediar las calificaciones de voto de ambas fuentes`() = runTest {
        val tmdbMovie = createTestMovie(voteAverage = 8.5)
        val omdbMovie = createTestMovie(voteAverage = 7.5)
        val tmdbSource = FakeMovieDetailExternalSource(tmdbMovie)
        val omdbSource = FakeMovieDetailExternalSource(omdbMovie)
        val broker = MovieDetailExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieDetail("TestMovie")

        assertEquals(8.0, result?.voteAverage)
    }

    @Test
    fun `obtenerDetallePelícula debe preservar fecha de lanzamiento de TMDB al combinar resultados`() = runTest {
        val tmdbMovie = createTestMovie(releaseDate = "2023-05-15")
        val omdbMovie = createTestMovie(releaseDate = "2023-05-20")
        val tmdbSource = FakeMovieDetailExternalSource(tmdbMovie)
        val omdbSource = FakeMovieDetailExternalSource(omdbMovie)
        val broker = MovieDetailExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieDetail("TestMovie")

        assertEquals("2023-05-15", result?.releaseDate)
    }

    @Test
    fun `obtenerDetallePelícula debe usar fecha de lanzamiento de OMDB cuando la fecha de TMDB está vacía`() = runTest {
        val tmdbMovie = createTestMovie(releaseDate = "")
        val omdbMovie = createTestMovie(releaseDate = "2023-05-20")
        val tmdbSource = FakeMovieDetailExternalSource(tmdbMovie)
        val omdbSource = FakeMovieDetailExternalSource(omdbMovie)
        val broker = MovieDetailExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieDetail("TestMovie")

        assertEquals("2023-05-20", result?.releaseDate)
    }

    @Test
    fun `obtenerDetallePelícula debe llamar a ambas fuentes con el mismo título`() = runTest {
        var tmdbCalledWith = ""
        var omdbCalledWith = ""

        val tmdbSource = object : MovieDetailExternalSource {
            override suspend fun getMovieDetail(title: String): Movie? {
                tmdbCalledWith = title
                return null
            }
        }

        val omdbSource = object : MovieDetailExternalSource {
            override suspend fun getMovieDetail(title: String): Movie? {
                omdbCalledWith = title
                return null
            }
        }

        val broker = MovieDetailExternalSourceBroker(tmdbSource, omdbSource)
        broker.getMovieDetail("TestMovie")

        assertEquals("TestMovie", tmdbCalledWith)
        assertEquals("TestMovie", omdbCalledWith)
    }

    @Test
    fun `obtenerDetallePelícula con título vacío`() = runTest {
        val tmdbMovie = createTestMovie(title = "", overview = "Test Overview")
        val tmdbSource = FakeMovieDetailExternalSource(tmdbMovie)
        val omdbSource = FakeMovieDetailExternalSource(null)
        val broker = MovieDetailExternalSourceBroker(tmdbSource, omdbSource)

        val result = broker.getMovieDetail("")

        assertEquals(tmdbMovie.id, result?.id)
        assertEquals("TMDB: Test Overview", result?.overview)
    }
}



