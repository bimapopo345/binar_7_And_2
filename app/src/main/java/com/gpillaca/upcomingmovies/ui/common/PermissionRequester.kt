package com.gpillaca.upcomingmovies.ui.common

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PermissionRequester(
    fragment: Fragment,
    private val permission: String
) {
    private var onRequest: (Boolean) -> Unit = {}

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            onRequest(isGranted)
        }

    suspend fun requestPermission(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            onRequest = {
                continuation.resume(it)
            }
            requestPermissionLauncher.launch(permission)
        }
    }
}
