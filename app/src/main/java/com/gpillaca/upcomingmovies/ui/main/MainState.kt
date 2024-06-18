package com.gpillaca.upcomingmovies.ui.main

import android.Manifest
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.gpillaca.upcomingmovies.R
import com.gpillaca.upcomingmovies.ui.common.PermissionRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.gpillaca.upcomingmovies.domain.common.Error

class MainState(
    private val context: Context,
    private val scope: CoroutineScope,
    private val locationPermissionRequester: PermissionRequester,
    private val navController: NavController
) {

    fun onMovieClick(movieId: Int) {
        val navAction = MainFragmentDirections.actionMainToMoviedetail(movieId)
        navController.navigate(navAction)
    }

    fun requestLocationPermissions(afterRequest: (Boolean) -> Unit) {
        scope.launch {
            val result = locationPermissionRequester.requestPermission()
            afterRequest(result)
        }
    }

    fun errorToString(error: Error) = when (error) {
        Error.Connectivity -> context.getString(R.string.connectivity_error)
        is Error.Server -> context.getString(R.string.server_error) + error.code
        is Error.Unknown -> context.getString(R.string.unknown_error) + error.message
    }
}

fun Fragment.buildMainState(
    context: Context = requireContext(),
    scope: CoroutineScope = viewLifecycleOwner.lifecycleScope,
    locationPermissionRequester: PermissionRequester = PermissionRequester(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ),
    navController: NavController = findNavController()
) = MainState(context, scope, locationPermissionRequester, navController)
