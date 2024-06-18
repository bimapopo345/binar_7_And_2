package com.gpillaca.upcomingmovies.ui.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.gpillaca.upcomingmovies.data.InternetConnectionChecker

@Suppress("DEPRECATION")
class AppInternetConnectionChecker(private val context: Context) : InternetConnectionChecker {
    override fun isInternetAvailable() = isOnline()

    /**
     * Checks if there is an active internet connection
     * @return true If there is an active internet connection
     */
    private fun isOnline(): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
}
