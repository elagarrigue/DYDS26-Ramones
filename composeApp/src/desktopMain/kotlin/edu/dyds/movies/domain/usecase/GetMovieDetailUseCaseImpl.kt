package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class GetMovieDetailUseCaseImpl(private val moviesRepository: MoviesRepository) : GetMovieDetailUseCase {
    override suspend operator fun invoke(id: Int): Movie? {
        return moviesRepository.getMovieDetails(id)
    }
}
