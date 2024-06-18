package com.gpillaca.upcomingmovies.viewmodel

import app.cash.turbine.test
import com.gpillaca.upcomingmovies.CoroutineTestRule
import com.gpillaca.upcomingmovies.domain.common.Either
import com.gpillaca.upcomingmovies.stubs.movieStub
import com.gpillaca.upcomingmovies.ui.main.MainViewModel
import com.gpillaca.upcomingmovies.ui.main.MainViewModel.UiState
import com.gpillaca.upcomingmovies.usecases.GetPopularMoviesUseCase
import com.gpillaca.upcomingmovies.usecases.RequestPopularMoviesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

/**
 * Test class of [MovieDetailViewModel]
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    lateinit var requestPopularMoviesUseCase: RequestPopularMoviesUseCase

    @Mock
    lateinit var getPopularMoviesUseCase: GetPopularMoviesUseCase
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(requestPopularMoviesUseCase, getPopularMoviesUseCase)
    }

    @Test
    fun `State is updated with current cached content immediately`() = runTest {
        val results = mutableListOf<UiState>()
        val movies = listOf(movieStub.copy(id = 5))

        mainViewModel.onUiReady()
        whenever(getPopularMoviesUseCase()).thenReturn(flowOf(movies))

        val job = launch {
            mainViewModel.state.toList(results)
        }
        runCurrent()
        job.cancel()

        assertEquals(UiState(movies = movies), results.first())
    }

    @Test
    fun `Turbine_ State is updated with current cached content immediately`() = runTest {
        val movies = listOf(movieStub.copy(id = 5))
        mainViewModel.onUiReady()
        whenever(getPopularMoviesUseCase()).thenReturn(flowOf(movies))

        mainViewModel.state.test {
            assertEquals(UiState(), awaitItem())
            assertEquals(UiState(movies = movies), awaitItem())
            cancel()
        }
    }

    @Test
    fun `Requesting movies when cache content is empty`() = runTest {
        mainViewModel.onUiReady()
        whenever(getPopularMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(requestPopularMoviesUseCase()).thenReturn(Either.Left(null))

        mainViewModel.state.test {
            assertEquals(UiState(), awaitItem())
            assertEquals(UiState(loading = true), awaitItem())
            assertEquals(UiState(loading = false), awaitItem())
            cancel()
        }
    }

    @Test
    fun `Progress is shown when screen start and hidden when it finishes requesting movies`() = runTest {
        mainViewModel.requestPopularMovies()
        whenever(requestPopularMoviesUseCase()).thenReturn(Either.Left(null))
        mainViewModel.state.test {
            assertEquals(UiState(), awaitItem())
            assertEquals(UiState(loading = true), awaitItem())
            assertEquals(UiState(loading = false), awaitItem())
            cancel()
        }
    }
}
