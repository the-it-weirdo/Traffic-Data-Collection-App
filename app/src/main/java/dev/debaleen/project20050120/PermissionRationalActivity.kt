package dev.debaleen.project20050120

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class PermissionRationalActivity : AppCompatActivity(),
    ActivityCompat.OnRequestPermissionsResultCallback {

    private val TAG = PermissionRationalActivity::class.java.simpleName

    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    /* Id to identify Activity Recognition permission request. */
    private val PERMISSION_REQUEST_ACTIVITY_RECOGNITION_AND_WRITE_EXTERNAL = 45

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If permissions granted, we start the main activity (shut this activity down).
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    == PackageManager.PERMISSION_GRANTED
                    ) && (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                    == PackageManager.PERMISSION_GRANTED)
        ) {
            finish()
        }
        setContentView(R.layout.activity_permission_rational)
    }

    fun onClickApprovePermissionRequest(view: View?) {
        Log.d(TAG, "onClickApprovePermissionRequest()")

        // Review permission request for activity recognition.
        if (runningQOrLater)
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_ACTIVITY_RECOGNITION_AND_WRITE_EXTERNAL
            )
        else
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_ACTIVITY_RECOGNITION_AND_WRITE_EXTERNAL
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
        if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION_AND_WRITE_EXTERNAL) {
            // Close activity regardless of user's decision (decision picked up in main activity).
            finish()
        }
    }
}