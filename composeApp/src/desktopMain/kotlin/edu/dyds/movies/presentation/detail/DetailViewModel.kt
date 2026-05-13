package edu.dyds.movies.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.usecase.GetMovieDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val getMovieDetailUseCase: GetMovieDetailUseCase
) : ViewModel() {

    private val movieDetailStateMutableStateFlow = MutableStateFlow(DetailUiState())

    val movieDetailStateFlow: StateFlow<DetailUiState> = movieDetailStateMutableStateFlow

    fun getMovieDetail(id: Int) {
        viewModelScope.launch {
            movieDetailStateMutableStateFlow.emit(
                DetailUiState(isLoading = true)
            )
            movieDetailStateMutableStateFlow.emit(
                DetailUiState(
                    isLoading = false,
                    movie = getMovieDetailUseCase(id)
                )
            )
        }
    }

    data class DetailUiState(
        val isLoading: Boolean = false,
        val movie: Movie? = null,
    )
}

