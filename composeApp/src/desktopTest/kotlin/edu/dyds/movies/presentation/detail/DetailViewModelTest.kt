package edu.dyds.movies.presentation.detail

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.usecase.GetMovieDetailUseCase
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val movie = Movie(
        id = 1,
        title = "Test Movie",
        overview = "A test movie overview",
        releaseDate = "2024-01-01",
        poster = "/poster.jpg",
        backdrop = "/backdrop.jpg",
        originalTitle = "Test Movie",
        originalLanguage = "en",
        popularity = 80.0,
        voteAverage = 7.5
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun fakeUseCase(result: Movie?): GetMovieDetailUseCase =
        object : GetMovieDetailUseCase {
            override suspend fun invoke(id: Int): Movie? = result
        }

    @Test
    fun `estado inicial tiene isLoading en false y movie en null`() = runTest {
        val viewModel = DetailViewModel(fakeUseCase(movie))

        val state = viewModel.movieDetailStateFlow.value

        assertFalse(state.isLoading)
        assertNull(state.movie)
    }

    @Test
    fun `getMovieDetail termina con isLoading en false tras obtener la pelicula`() = runTest {
        val viewModel = DetailViewModel(fakeUseCase(movie))

        viewModel.getMovieDetail(movie.id)

        assertFalse(viewModel.movieDetailStateFlow.value.isLoading)
    }

    @Test
    fun `getMovieDetail actualiza el estado con la pelicula cuando el caso de uso la retorna`() = runTest {
        val viewModel = DetailViewModel(fakeUseCase(movie))

        viewModel.getMovieDetail(movie.id)

        val state = viewModel.movieDetailStateFlow.value
        assertEquals(movie, state.movie)
        assertFalse(state.isLoading)
    }

    @Test
    fun `getMovieDetail deja movie en null cuando el caso de uso no encuentra la pelicula`() = runTest {
        val viewModel = DetailViewModel(fakeUseCase(null))

        viewModel.getMovieDetail(999)

        val state = viewModel.movieDetailStateFlow.value
        assertNull(state.movie)
        assertFalse(state.isLoading)
    }

    @Test
    fun `getMovieDetail pasa el id correcto al caso de uso`() = runTest {
        var capturedId: Int? = null
        val useCase = object : GetMovieDetailUseCase {
            override suspend fun invoke(id: Int): Movie? {
                capturedId = id
                return movie
            }
        }
        val viewModel = DetailViewModel(useCase)

        viewModel.getMovieDetail(42)

        assertEquals(42, capturedId)
    }

    @Test
    fun `getMovieDetail reemplaza la pelicula anterior al llamarse de nuevo`() = runTest {
        val secondMovie = movie.copy(id = 2, title = "New Movie")
        val useCase = object : GetMovieDetailUseCase {
            override suspend fun invoke(id: Int): Movie? =
                if (id == movie.id) movie else secondMovie
        }
        val viewModel = DetailViewModel(useCase)

        viewModel.getMovieDetail(movie.id)
        viewModel.getMovieDetail(2)

        assertEquals(secondMovie, viewModel.movieDetailStateFlow.value.movie)
    }
}
