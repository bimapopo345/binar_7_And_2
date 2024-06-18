package com.gpillaca.upcomingmovies.framework.server

import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteService {

    @GET("discover/movie?sort_by=popularity.desc")
    suspend fun listPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): RemoteResult

}