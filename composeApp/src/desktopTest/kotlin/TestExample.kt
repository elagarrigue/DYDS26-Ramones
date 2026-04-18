package edu.dyds.movies

import edu.dyds.movies.data.MoviesRepositoryImpl
import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.external.RemoteMoviesDataSource
import edu.dyds.movies.data.local.LocalMoviesCache
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.repository.MoviesRepository
import edu.dyds.movies.domain.usecase.GetMovieDetailUseCase
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import edu.dyds.movies.presentation.MoviesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TestExample {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `repository uses local cache when available`() = runTest {
        val cachedMovies = listOf(
            remoteMovie(id = 1, voteAverage = 5.0),
            remoteMovie(id = 2, voteAverage = 8.5)
        )
        val remoteDataSource = FakeRemoteMoviesDataSource(popularMovies = listOf(remoteMovie(id = 3, voteAverage = 9.0)))
        val localCache = FakeLocalMoviesCache(cachedMovies = cachedMovies)
        val repository = MoviesRepositoryImpl(remoteDataSource, localCache)

        val result = repository.getPopularMovies()

        assertEquals(0, remoteDataSource.getPopularMoviesCalls)
        assertEquals(2, result.first().movie.id)
        assertTrue(result.first().isGoodMovie)
        assertFalse(result.last().isGoodMovie)
    }

    @Test
    fun `repository fetches remote and saves cache when local cache is empty`() = runTest {
        val remoteMovies = listOf(
            remoteMovie(id = 10, voteAverage = 7.0),
            remoteMovie(id = 11, voteAverage = 6.0)
        )
        val remoteDataSource = FakeRemoteMoviesDataSource(popularMovies = remoteMovies)
        val localCache = FakeLocalMoviesCache(cachedMovies = null)
        val repository = MoviesRepositoryImpl(remoteDataSource, localCache)

        val result = repository.getPopularMovies()

        assertEquals(1, remoteDataSource.getPopularMoviesCalls)
        assertEquals(2, localCache.savedMovies?.size)
        assertEquals(listOf(10, 11), result.map { it.movie.id })
    }

    @Test
    fun `repository maps movie detail from remote to domain`() = runTest {
        val remoteMovie = remoteMovie(id = 99, voteAverage = 9.1)
        val remoteDataSource = FakeRemoteMoviesDataSource(movieDetail = remoteMovie)
        val repository = MoviesRepositoryImpl(remoteDataSource, FakeLocalMoviesCache())

        val detail = repository.getMovieDetails(99)

        assertNotNull(detail)
        assertEquals(99, detail.id)
        assertTrue(detail.poster.startsWith("https://image.tmdb.org/t/p/w185"))
    }

    @Test
    fun `viewmodel emits loading and loaded states for movie list`() = runTest {
        val repository = FakeMoviesRepository(
            popularMovies = listOf(
                QualifiedMovie(movie = domainMovie(id = 7, voteAverage = 8.0), isGoodMovie = true)
            )
        )
        val viewModel = MoviesViewModel(
            getPopularMoviesUseCase = GetPopularMoviesUseCase(repository),
            getMovieDetailUseCase = GetMovieDetailUseCase(repository)
        )

        val states = mutableListOf<MoviesViewModel.MoviesUiState>()
        val collectionJob = launch {
            viewModel.moviesStateFlow.take(3).toList(states)
        }

        viewModel.getAllMovies()
        advanceUntilIdle()
        collectionJob.join()

        assertTrue(states.any { it.isLoading })
        assertEquals(1, states.last().movies.size)
        assertFalse(states.last().isLoading)
    }

    @Test
    fun `viewmodel emits loading and loaded states for movie detail`() = runTest {
        val repository = FakeMoviesRepository(movieDetail = domainMovie(id = 42, voteAverage = 9.0))
        val viewModel = MoviesViewModel(
            getPopularMoviesUseCase = GetPopularMoviesUseCase(repository),
            getMovieDetailUseCase = GetMovieDetailUseCase(repository)
        )

        val firstStateJob = launch {
            // Esperamos al menos el estado inicial y el estado final no-loading.
            viewModel.movieDetailStateFlow.take(3).toList()
        }

        viewModel.getMovieDetail(42)
        advanceUntilIdle()
        firstStateJob.join()

        val finalState = viewModel.movieDetailStateFlow.first()
        assertFalse(finalState.isLoading)
        assertEquals(42, finalState.movie?.id)
    }

    private fun remoteMovie(id: Int, voteAverage: Double): RemoteMovie {
        return RemoteMovie(
            id = id,
            title = "Movie $id",
            overview = "Overview $id",
            releaseDate = "2026-01-01",
            posterPath = "/poster$id.jpg",
            backdropPath = "/backdrop$id.jpg",
            originalTitle = "Original $id",
            originalLanguage = "en",
            popularity = 100.0 + id,
            voteAverage = voteAverage
        )
    }

    private fun domainMovie(id: Int, voteAverage: Double): Movie {
        return Movie(
            id = id,
            title = "Movie $id",
            overview = "Overview $id",
            releaseDate = "2026-01-01",
            poster = "https://image.tmdb.org/t/p/w185/poster$id.jpg",
            backdrop = "https://image.tmdb.org/t/p/w780/backdrop$id.jpg",
            originalTitle = "Original $id",
            originalLanguage = "en",
            popularity = 100.0 + id,
            voteAverage = voteAverage
        )
    }
}

private class FakeRemoteMoviesDataSource(
    private val popularMovies: List<RemoteMovie> = emptyList(),
    private val movieDetail: RemoteMovie? = null
) : RemoteMoviesDataSource {
    var getPopularMoviesCalls: Int = 0

    override suspend fun getPopularMovies(): List<RemoteMovie> {
        getPopularMoviesCalls += 1
        return popularMovies
    }

    override suspend fun getMovieDetails(id: Int): RemoteMovie? {
        return movieDetail
    }
}

private class FakeLocalMoviesCache(
    private var cachedMovies: List<RemoteMovie>? = null
) : LocalMoviesCache {
    var savedMovies: List<RemoteMovie>? = null

    override fun getCachedMovies(): List<RemoteMovie>? {
        return cachedMovies
    }

    override fun saveMovies(movies: List<RemoteMovie>) {
        savedMovies = movies
        cachedMovies = movies
    }

    override fun clearCache() {
        cachedMovies = null
    }
}

private class FakeMoviesRepository(
    private val popularMovies: List<QualifiedMovie> = emptyList(),
    private val movieDetail: Movie? = null
) : MoviesRepository {
    override suspend fun getPopularMovies(): List<QualifiedMovie> {
        return popularMovies
    }

    override suspend fun getMovieDetails(id: Int): Movie? {
        return movieDetail
    }
}