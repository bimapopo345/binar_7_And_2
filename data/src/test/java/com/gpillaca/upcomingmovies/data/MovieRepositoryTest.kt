package com.gpillaca.upcomingmovies.data

import com.gpillaca.upcomingmovies.data.datasource.MovieLocalDataSource
import com.gpillaca.upcomingmovies.data.datasource.MovieRemoteDataSource
import com.gpillaca.upcomingmovies.data.stubs.DEFAULT_LANGUAGE
import com.gpillaca.upcomingmovies.data.stubs.movieStub
import com.gpillaca.upcomingmovies.domain.common.right
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Test class of [MovieRepository]
 */
@RunWith(MockitoJUnitRunner::class)
class MovieRepositoryTest {

    @Mock
    lateinit var regionRepository: RegionRepository

    @Mock
    lateinit var remoteDataSource: MovieRemoteDataSource

    @Mock
    lateinit var localDataSource: MovieLocalDataSource

    private lateinit var moviesRepository: MovieRepository

    private val movieList = flowOf(listOf(movieStub.copy(id = 1)))

    @Before
    fun setUp() {
        whenever(localDataSource.movies).thenReturn(movieList)
        moviesRepository = MovieRepository(
            regionRepository,
            localDataSource,
            remoteDataSource
        )
    }

    @Test
    fun `Popular movies are taken from local data source in available`(): Unit = runBlocking {
        val resultFlow = moviesRepository.popularMovies
        assertEquals(movieList, resultFlow)
    }

    @Test
    fun `Popular movies are saved to local data source when it's empty`(): Unit = runBlocking {
        val movies = listOf(movieStub.copy(id = 5))
        whenever(regionRepository.findLastLanguage()).thenReturn(DEFAULT_LANGUAGE)
        whenever(remoteDataSource.findPopularMovies(any())).thenReturn(movies.right())
        whenever(localDataSource.save(movies)).thenReturn(Unit.right())

        moviesRepository.requestPopularMovies()

        verify(localDataSource).save(movies)
    }

    @Test
    fun `finding a movie by id is done in local data source`(): Unit = runBlocking {
        val movie = flowOf(movieStub.copy(id = 5))
        whenever(localDataSource.findMovie(5)).thenReturn(movie)

        val result = moviesRepository.findMovie(5)

        assertEquals(movie, result)
    }

    @Test
    fun `Switching favorite updates local data source`(): Unit = runBlocking {
        val movie = movieStub.copy(id = 5)

        moviesRepository.switchFavorite(movie)

        verify(localDataSource).updateMovie(argThat { id == 5 })
    }

    @Test
    fun `Switching favorite marks as favorite an unfavorite movie`(): Unit = runBlocking {
        val movie = movieStub.copy(favorite = false)

        moviesRepository.switchFavorite(movie)

        verify(localDataSource).updateMovie(argThat { favorite })
    }

    @Test
    fun `Switching favorite marks as unfavorite an favorite movie`(): Unit = runBlocking {
        val movie = movieStub.copy(favorite = true)

        moviesRepository.switchFavorite(movie)

        verify(localDataSource).updateMovie(argThat { !favorite })
    }

}
