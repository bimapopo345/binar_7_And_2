package com.gpillaca.upcomingmovies.usecases

import com.gpillaca.upcomingmovies.domain.Movie
import com.gpillaca.upcomingmovies.data.MovieRepository
import javax.inject.Inject

/**
 * Test class [SwitchMovieFavoriteUseCaseTest]
 */
class SwitchMovieFavoriteUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {

    suspend operator fun invoke(movie: Movie) {
        movieRepository.switchFavorite(movie)
    }
}
