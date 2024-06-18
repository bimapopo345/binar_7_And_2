package com.gpillaca.upcomingmovies.domain.common

sealed interface Error {
    data class Server(val code: Int): Error
    data object Connectivity: Error
    data class Unknown(val message: String): Error
}
