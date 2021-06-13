package dev.debaleen.project20050120

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import dev.debaleen.project20050120.util.appRequiredPermissionsApproved

class PermissionRationalActivity : AppCompatActivity(),
    ActivityCompat.OnRequestPermissionsResultCallback {

    private val TAG = PermissionRationalActivity::class.java.simpleName

    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    /* Id to identify Activity Recognition permission request. */
    private val requiredPermissionsRequestId = 45

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If permissions granted, we start the main activity (shut this activity down).
        if (appRequiredPermissionsApproved(runningQOrLater)) {
            finish()
        }
        setContentView(R.layout.activity_permission_rational)
    }

    fun onClickApprovePermissionRequest(view: View?) {
        Log.d(TAG, "onClickApprovePermissionRequest()")

        val requiredPermissions = mutableListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )

        // Review permission request for activity recognition.
        if (runningQOrLater) {
            requiredPermissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
            ActivityCompat.requestPermissions(
                this, requiredPermissions.toTypedArray(), requiredPermissionsRequestId
            )
        } else
            ActivityCompat.requestPermissions(
                this, requiredPermissions.toTypedArray(), requiredPermissionsRequestId
            )
    }

    fun onClickDenyPermissionRequest(view: View?) {
        Log.d(TAG, "onClickDenyPermissionRequest()")
        finish()
    }

    /*
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        val permissionResult = "Request code: " + requestCode + ", Permissions: " +
                permissions.contentToString() + ", Results: " + grantResults.contentToString()
        Log.d(TAG, "onRequestPermissionsResult(): $permissionResult")
        if (requestCode == requiredPermissionsRequestId) {
            // Close activity regardless of user's decision (decision picked up in main activity).
            finish()
        }
    }
}