package com.gpillaca.upcomingmovies.data

import com.gpillaca.upcomingmovies.data.datasource.LocationDataSource
import com.gpillaca.upcomingmovies.data.PermissionChecker.Permission.COARSE_LOCATION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Test class [RegionRepositoryTest]
 */
class RegionRepository @Inject constructor(
    private val permissionChecker: PermissionChecker,
    private val locationDataSource: LocationDataSource,
    private  val internetConnectionChecker: InternetConnectionChecker
) {

    companion object {
        private const val DEFAULT_LANGUAGE = "en-US"
    }

    suspend fun findLastLanguage(): String = withContext(Dispatchers.IO) {
        if (permissionChecker.check(COARSE_LOCATION) && internetConnectionChecker.isInternetAvailable()) {
            locationDataSource.findLastLanguage() ?: DEFAULT_LANGUAGE
        } else {
            DEFAULT_LANGUAGE
        }
    }
}
