package dev.debaleen.project20050120.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import androidx.core.app.ActivityCompat

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.disable() {
    isEnabled = false
}

fun View.enable() {
    isEnabled = true
}

fun Context.appRequiredPermissionsApproved(runningQOrLater: Boolean): Boolean {
    val permissionsGranted =
        PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) && PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )


    // Review permission check for 29+.
    return if (runningQOrLater) {
        PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) && permissionsGranted
    } else {
        permissionsGranted
    }
}