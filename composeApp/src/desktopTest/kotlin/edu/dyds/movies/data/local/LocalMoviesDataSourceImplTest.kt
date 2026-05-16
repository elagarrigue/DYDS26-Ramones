package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LocalMoviesDataSourceImplTest {

    private lateinit var dataSource: LocalMoviesDataSourceImpl

    @BeforeTest
    fun setUp() {
        dataSource = LocalMoviesDataSourceImpl()
    }

    private fun makeMovie(id: Int) = Movie(
        id = id,
        title = "Movie $id",
        overview = "",
        releaseDate = "2024-01-01",
        poster = "",
        backdrop = null,
        originalTitle = "Movie $id",
        originalLanguage = "en",
        popularity = 100.0,
        voteAverage = 7.0
    )

    @Test
    fun `getCachedMovies retorna null cuando el cache esta vacio`() {
        val result = dataSource.getCachedMovies()

        assertNull(result)
    }

    @Test
    fun `getCachedMovies retorna las peliculas guardadas`() {
        val movies = listOf(makeMovie(1), makeMovie(2))
        dataSource.saveMovies(movies)

        val result = dataSource.getCachedMovies()

        assertEquals(movies, result)
    }

    @Test
    fun `saveMovies guarda correctamente las peliculas`() {
        val movies = listOf(makeMovie(1), makeMovie(2), makeMovie(3))

        dataSource.saveMovies(movies)

        assertEquals(3, dataSource.getCachedMovies()?.size)
        assertEquals(1, dataSource.getCachedMovies()?.get(0)?.id)
    }

    @Test
    fun `saveMovies reemplaza el contenido anterior`() {
        dataSource.saveMovies(listOf(makeMovie(1), makeMovie(2)))

        dataSource.saveMovies(listOf(makeMovie(99)))

        val result = dataSource.getCachedMovies()
        assertEquals(1, result?.size)
        assertEquals(99, result?.first()?.id)
    }
}

