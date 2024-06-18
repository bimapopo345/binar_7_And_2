package com.gpillaca.upcomingmovies.framework.database

import com.gpillaca.upcomingmovies.domain.common.Either
import com.gpillaca.upcomingmovies.mappers.MovieMapper
import com.gpillaca.upcomingmovies.data.datasource.MovieLocalDataSource
import com.gpillaca.upcomingmovies.domain.Movie
import com.gpillaca.upcomingmovies.domain.common.Error
import com.gpillaca.upcomingmovies.framework.catch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieRoomDataSource @Inject constructor(
    private val movieDao: MovieDao,
    private val movieMapper: MovieMapper
) : MovieLocalDataSource {

    override val movies: Flow<List<Movie>> = movieDao.getAll().map {
        movieMapper.fromDBToMovieListDomain(it)
    }

    override suspend fun save(movies: List<Movie>): Either<Error, Unit> = catch {
        if (isEmpty()) {
            movieDao.insertMovies(movieMapper.fromDomainToMoviesDatabase(movies))
        } else {
            var localMovies: List<MovieDB> = movieDao.getAllMovies()
            var remoteMovies = movieMapper.fromDomainToMoviesDatabase(movies)

            if (localMovies != remoteMovies) {
                remoteMovies = remoteMovies.map { remoteMovie ->
                    localMovies.first { remoteMovie.id == it.id }.let {
                        remoteMovie.copy(favorite = it.favorite)
                    }
                }
            }
            movieDao.insertMovies(remoteMovies)
        }

    }

    override suspend fun isEmpty(): Boolean = movieDao.movieCount() == 0

    override fun findMovie(id: Int) = movieDao.findByID(id).map {
        movieMapper.fromDBToMovieDomain(it)
    }

    override suspend fun updateMovie(movie: Movie) = movieDao.updateMovie(
        movieMapper.fromDomainToMovieDatabase(movie)
    )
}
