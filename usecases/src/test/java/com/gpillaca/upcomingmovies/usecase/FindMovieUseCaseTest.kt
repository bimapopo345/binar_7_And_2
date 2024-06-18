package com.gpillaca.upcomingmovies.usecase

import com.gpillaca.upcomingmovies.data.MovieRepository
import com.gpillaca.upcomingmovies.usecase.stubs.movieStub
import com.gpillaca.upcomingmovies.usecases.FindMovieUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

/**
 * Test class of [FindMovieUseCase]
 */
@RunWith(MockitoJUnitRunner::class)
class FindMovieUseCaseTest {

    @Mock
    lateinit var movieRepository: MovieRepository

    lateinit var findMovieUseCase: FindMovieUseCase

    @Before
    fun setUp() {
        findMovieUseCase = FindMovieUseCase(movieRepository)
    }

    @Test
    fun `Invoke calls movies repository`() = runBlocking {
        val movie = flowOf(movieStub.copy(id = 5))
        whenever(movieRepository.findMovie(5)).thenReturn(movie)

        val result = findMovieUseCase(5)

        assertEquals(movie, result)
    }
}
