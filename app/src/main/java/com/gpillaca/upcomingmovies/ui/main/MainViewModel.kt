package com.gpillaca.upcomingmovies.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gpillaca.upcomingmovies.framework.toError
import com.gpillaca.upcomingmovies.domain.Movie
import com.gpillaca.upcomingmovies.domain.common.Error
import com.gpillaca.upcomingmovies.usecases.GetPopularMoviesUseCase
import com.gpillaca.upcomingmovies.usecases.RequestPopularMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Test class [MainViewModelTest]
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val requestPopularMoviesUseCase: RequestPopularMoviesUseCase,
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val movies: List<Movie>? = null,
        val error: Error? = null
    )

    private var _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun onUiReady() {
        viewModelScope.launch {
            getPopularMoviesUseCase()
                .catch { cause ->
                    _state.update { it.copy(error = cause.toError()) }
                }
                .collect { movies ->
                    when {
                        movies.isEmpty() -> {
                            requestPopularMovies()
                        }
                        else -> {
                            _state.update { UiState(movies = movies) }
                        }
                    }
                }
        }
    }

    fun requestPopularMovies() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            requestPopularMoviesUseCase().onLeft { error ->
                _state.update { _state.value.copy(loading = false, error = error) }
            }
        }
    }
}
