package com.gpillaca.upcomingmovies.data

import com.gpillaca.upcomingmovies.data.PermissionChecker.Permission.COARSE_LOCATION
import com.gpillaca.upcomingmovies.data.datasource.LocationDataSource
import com.gpillaca.upcomingmovies.data.stubs.DEFAULT_LANGUAGE
import com.gpillaca.upcomingmovies.data.stubs.ES_LANGUAGE
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

/**
 * Test class of [RegionRepository]
 */
@RunWith(MockitoJUnitRunner::class)
class RegionRepositoryTest {

    @Mock
    lateinit var permissionChecker: PermissionChecker

    @Mock
    lateinit var locationDataSource: LocationDataSource

    @Mock
    lateinit var internetConnectionChecker: InternetConnectionChecker

    private lateinit var regionRepository: RegionRepository

    @Before
    fun setUp() {
        regionRepository = RegionRepository(permissionChecker, locationDataSource, internetConnectionChecker)
    }

    @Test
    fun `Returns default language when coarse permission not granted`(): Unit = runBlocking {
        whenever(permissionChecker.check(COARSE_LOCATION)).thenReturn(false)
        val language = regionRepository.findLastLanguage()

        assertEquals(DEFAULT_LANGUAGE, language)
    }

    @Test
    fun `Returns language from location data source when permission granted`(): Unit = runBlocking {
        whenever(permissionChecker.check(COARSE_LOCATION)).thenReturn(true)
        whenever(internetConnectionChecker.isInternetAvailable()).thenReturn(true)
        whenever(locationDataSource.findLastLanguage()).thenReturn(ES_LANGUAGE)

        val language = regionRepository.findLastLanguage()

        assertEquals(ES_LANGUAGE, language)
    }
}
