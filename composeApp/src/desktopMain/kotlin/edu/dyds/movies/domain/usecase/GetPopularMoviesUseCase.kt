package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.QualifiedMovie

interface GetPopularMoviesUseCase {
    suspend operator fun invoke(): List<QualifiedMovie>
}
