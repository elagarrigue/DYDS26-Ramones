package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.repository.MoviesRepository

private const val MIN_VOTE_AVERAGE = 6.0

class GetPopularMoviesUseCaseImpl(private val moviesRepository: MoviesRepository) : GetPopularMoviesUseCase {
    override suspend operator fun invoke(): List<QualifiedMovie> {
        return moviesRepository.getPopularMovies()
            .sortedByDescending { it.voteAverage }
            .map { QualifiedMovie(movie = it, isGoodMovie = it.voteAverage >= MIN_VOTE_AVERAGE) }
    }
}
