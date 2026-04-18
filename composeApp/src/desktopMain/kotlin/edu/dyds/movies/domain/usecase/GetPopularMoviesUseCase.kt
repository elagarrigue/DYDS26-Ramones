package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.repository.MoviesRepository

class GetPopularMoviesUseCase(private val moviesRepository: MoviesRepository) {
    suspend operator fun invoke(): List<QualifiedMovie> {
        return moviesRepository.getPopularMovies()
    }
}
