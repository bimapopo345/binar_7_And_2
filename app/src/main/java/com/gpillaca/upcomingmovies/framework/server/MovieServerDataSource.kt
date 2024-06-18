package com.gpillaca.upcomingmovies.framework.server

import com.gpillaca.upcomingmovies.domain.common.Either
import com.gpillaca.upcomingmovies.mappers.MovieMapper
import com.gpillaca.upcomingmovies.data.datasource.MovieRemoteDataSource
import com.gpillaca.upcomingmovies.di.ApiKey
import com.gpillaca.upcomingmovies.domain.Movie
import com.gpillaca.upcomingmovies.domain.common.Error
import com.gpillaca.upcomingmovies.framework.catch
import javax.inject.Inject

class MovieServerDataSource @Inject constructor(
    @ApiKey private val apiKey: String,
    private val movieMapper: MovieMapper,
    private val remoteService: RemoteService
) : MovieRemoteDataSource {

    override suspend fun findPopularMovies(language: String): Either<Error, List<Movie>> = catch {
        val movieResponseList = remoteService.listPopularMovies(apiKey, language).results
        movieMapper.fromResponseToMovieListDomain(movieResponseList)
    }

}
