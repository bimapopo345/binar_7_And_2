package com.gpillaca.upcomingmovies.usecase

import com.gpillaca.upcomingmovies.data.MovieRepository
import com.gpillaca.upcomingmovies.usecase.stubs.movieStub
import com.gpillaca.upcomingmovies.usecases.SwitchMovieFavoriteUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

/**
 * Test class of [SwitchMovieFavoriteUseCase]
 */
@RunWith(MockitoJUnitRunner::class)
class SwitchMovieFavoriteUseCaseTest {

    @Mock
    lateinit var movieRepository: MovieRepository

    lateinit var switchMovieFavoriteUseCase: SwitchMovieFavoriteUseCase

    @Before
    fun setUp() {
        switchMovieFavoriteUseCase = SwitchMovieFavoriteUseCase(movieRepository)
    }

    @Test
    fun `Invoke calls movies repository`(): Unit = runBlocking {
        val movie = movieStub.copy(id = 5)
        switchMovieFavoriteUseCase(movie)

        verify(movieRepository).switchFavorite(movie)
    }
}
