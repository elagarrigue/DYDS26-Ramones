package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class GetMovieDetailUseCase(private val moviesRepository: MoviesRepository) {
    suspend operator fun invoke(id: Int): Movie? {
        return moviesRepository.getMovieDetails(id)
    }
}
