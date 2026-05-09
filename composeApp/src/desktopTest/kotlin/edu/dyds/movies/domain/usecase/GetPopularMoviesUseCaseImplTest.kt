package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetPopularMoviesUseCaseImplTest {

    private val goodMovie = Movie(
        id = 1,
        title = "Good Movie",
        overview = "",
        releaseDate = "2024-01-01",
        poster = "",
        backdrop = null,
        originalTitle = "Good Movie",
        originalLanguage = "en",
        popularity = 100.0,
        voteAverage = 7.0
    )

    private val badMovie = Movie(
        id = 2,
        title = "Bad Movie",
        overview = "",
        releaseDate = "2024-01-01",
        poster = "",
        backdrop = null,
        originalTitle = "Bad Movie",
        originalLanguage = "en",
        popularity = 50.0,
        voteAverage = 4.0
    )

    private val borderlineMovie = Movie(
        id = 3,
        title = "Borderline Movie",
        overview = "",
        releaseDate = "2024-01-01",
        poster = "",
        backdrop = null,
        originalTitle = "Borderline Movie",
        originalLanguage = "en",
        popularity = 75.0,
        voteAverage = 6.0
    )

    // --- FakeMoviesRepository ---

    private fun fakeRepository(movies: List<Movie>): MoviesRepository =
        object : MoviesRepository {
            override suspend fun getPopularMovies(): List<Movie> = movies
            override suspend fun getMovieDetails(id: Int): Movie? = null
        }

    // --- Tests ---

    @Test
    fun `las peliculas con voteAverage mayor a 6 se marcan como buenas`() = runTest {
        val useCase = GetPopularMoviesUseCaseImpl(fakeRepository(listOf(goodMovie)))

        val result = useCase()

        assertTrue(result.first().isGoodMovie)
    }

    @Test
    fun `las peliculas con voteAverage menor a 6 se marcan como malas`() = runTest {
        val useCase = GetPopularMoviesUseCaseImpl(fakeRepository(listOf(badMovie)))

        val result = useCase()

        assertTrue(!result.first().isGoodMovie)
    }

    @Test
    fun `las peliculas con voteAverage exactamente 6 se marcan como buenas`() = runTest {
        val useCase = GetPopularMoviesUseCaseImpl(fakeRepository(listOf(borderlineMovie)))

        val result = useCase()

        assertTrue(result.first().isGoodMovie)
    }

    @Test
    fun `las peliculas se ordenan por voteAverage de mayor a menor`() = runTest {
        val useCase = GetPopularMoviesUseCaseImpl(fakeRepository(listOf(badMovie, borderlineMovie, goodMovie)))

        val result = useCase()

        assertEquals(listOf(goodMovie, borderlineMovie, badMovie), result.map { it.movie })
    }

    @Test
    fun `una lista vacia retorna un resultado vacio`() = runTest {
        val useCase = GetPopularMoviesUseCaseImpl(fakeRepository(emptyList()))

        val result = useCase()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `el resultado contiene todas las peliculas del repositorio`() = runTest {
        val useCase = GetPopularMoviesUseCaseImpl(fakeRepository(listOf(goodMovie, badMovie)))

        val result = useCase()

        assertEquals(2, result.size)
    }
}
