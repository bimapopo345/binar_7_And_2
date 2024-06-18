package com.gpillaca.upcomingmovies.data.datasource

interface LocationDataSource {
    suspend fun findLastLanguage(): String?
}
