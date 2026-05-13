package edu.dyds.movies.presentation.home

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun makeMovie(id: Int, title: String, voteAverage: Double = 7.0) = Movie(
        id = id,
        title = title,
        overview = "",
        releaseDate = "2024-01-01",
        poster = "",
        backdrop = null,
        originalTitle = title,
        originalLanguage = "en",
        popularity = 80.0,
        voteAverage = voteAverage
    )

    private fun fakeUseCase(result: List<QualifiedMovie>): GetPopularMoviesUseCase =
        object : GetPopularMoviesUseCase {
            override suspend fun invoke(): List<QualifiedMovie> = result
        }

    // --- Tests ---

    @Test
    fun `estado inicial tiene isLoading en false y movies vacia`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(fakeUseCase(emptyList()))

        val state = viewModel.moviesStateFlow.value

        assertFalse(state.isLoading)
        assertTrue(state.movies.isEmpty())
    }

    @Test
    fun `getAllMovies termina con isLoading en false tras obtener las peliculas`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(fakeUseCase(emptyList()))

        viewModel.getAllMovies()

        assertFalse(viewModel.moviesStateFlow.value.isLoading)
    }

    @Test
    fun `getAllMovies actualiza el estado con la lista de peliculas retornada por el caso de uso`() = runTest(testDispatcher) {
        val qualifiedMovies = listOf(
            QualifiedMovie(makeMovie(1, "Movie 1"), true),
            QualifiedMovie(makeMovie(2, "Movie 2"), false)
        )
        val viewModel = HomeViewModel(fakeUseCase(qualifiedMovies))

        viewModel.getAllMovies()

        val state = viewModel.moviesStateFlow.value
        assertEquals(qualifiedMovies, state.movies)
        assertFalse(state.isLoading)
    }

    @Test
    fun `getAllMovies actualiza el estado con lista vacia cuando el caso de uso no retorna peliculas`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(fakeUseCase(emptyList()))

        viewModel.getAllMovies()

        val state = viewModel.moviesStateFlow.value
        assertTrue(state.movies.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun `getAllMovies preserva el orden de peliculas retornadas por el caso de uso`() = runTest(testDispatcher) {
        val qualifiedMovies = listOf(
            QualifiedMovie(makeMovie(1, "Best Movie", 9.0), true),
            QualifiedMovie(makeMovie(2, "Good Movie", 7.0), true),
            QualifiedMovie(makeMovie(3, "Bad Movie", 4.0), false),
        )
        val viewModel = HomeViewModel(fakeUseCase(qualifiedMovies))

        viewModel.getAllMovies()

        assertEquals(qualifiedMovies, viewModel.moviesStateFlow.value.movies)
    }

    @Test
    fun `getAllMovies retorna el numero correcto de peliculas`() = runTest(testDispatcher) {
        val qualifiedMovies = (1..5).map { QualifiedMovie(makeMovie(it, "Movie $it"), true) }
        val viewModel = HomeViewModel(fakeUseCase(qualifiedMovies))

        viewModel.getAllMovies()

        assertEquals(5, viewModel.moviesStateFlow.value.movies.size)
    }

    @Test
    fun `getAllMovies puede llamarse varias veces actualizando el estado correctamente`() = runTest(testDispatcher) {
        val firstBatch = listOf(QualifiedMovie(makeMovie(1, "Movie 1"), true))
        val secondBatch = listOf(
            QualifiedMovie(makeMovie(2, "Movie 2"), true),
            QualifiedMovie(makeMovie(3, "Movie 3"), false)
        )
        var callCount = 0
        val useCase = object : GetPopularMoviesUseCase {
            override suspend fun invoke(): List<QualifiedMovie> {
                callCount++
                return if (callCount == 1) firstBatch else secondBatch
            }
        }
        val viewModel = HomeViewModel(useCase)

        viewModel.getAllMovies()
        assertEquals(firstBatch, viewModel.moviesStateFlow.value.movies)

        viewModel.getAllMovies()
        assertEquals(secondBatch, viewModel.moviesStateFlow.value.movies)
    }

    @Test
    fun `getAllMovies reemplaza la lista anterior al llamarse de nuevo`() = runTest(testDispatcher) {
        val firstBatch = listOf(QualifiedMovie(makeMovie(1, "Old Movie"), true))
        val secondBatch = listOf(QualifiedMovie(makeMovie(2, "New Movie"), false))
        var callCount = 0
        val useCase = object : GetPopularMoviesUseCase {
            override suspend fun invoke(): List<QualifiedMovie> {
                callCount++
                return if (callCount == 1) firstBatch else secondBatch
            }
        }
        val viewModel = HomeViewModel(useCase)

        viewModel.getAllMovies()
        viewModel.getAllMovies()

        assertEquals(secondBatch, viewModel.moviesStateFlow.value.movies)
    }
}
