package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetMovieDetailUseCaseImplTest {

    private val movie = Movie(
        id = 1,
        title = "Una pelicula",
        overview = "Descripcion",
        releaseDate = "2024-01-01",
        poster = "",
        backdrop = null,
        originalTitle = "Una pelicula",
        originalLanguage = "en",
        popularity = 80.0,
        voteAverage = 7.5
    )

    // --- FakeMoviesRepository ---

    private fun fakeRepository(movieDetail: Movie?): MoviesRepository =
        object : MoviesRepository {
            override suspend fun getPopularMovies(): List<Movie> = emptyList()
            override suspend fun getMovieDetails(id: Int): Movie? = movieDetail
        }

    // --- Tests ---

    @Test
    fun `retorna la pelicula cuando el repositorio la encuentra`() = runTest {
        val useCase = GetMovieDetailUseCaseImpl(fakeRepository(movie))

        val result = useCase(movie.id)

        assertEquals(movie, result)
    }

    @Test
    fun `retorna null cuando el repositorio no encuentra la pelicula`() = runTest {
        val useCase = GetMovieDetailUseCaseImpl(fakeRepository(null))

        val result = useCase(1)

        assertNull(result)
    }
}

