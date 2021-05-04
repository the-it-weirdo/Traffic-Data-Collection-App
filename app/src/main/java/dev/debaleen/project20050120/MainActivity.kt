package dev.debaleen.project20050120

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import dev.debaleen.project20050120.activityRecognition.activityDetection.DetectActivitiesService
import dev.debaleen.project20050120.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        var KillRequestFromMainActivity: Boolean = false
        private val TAG = MainActivity::class.java.simpleName
    }

    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.startStopBtn.setOnClickListener {
            // Request permission and start activity for result. If the permission is approved, we
            // want to make sure we start activity recognition tracking.
            if (appRequiredPermissionsApproved()) {
                startStop()
            } else {
                val startIntent = Intent(this, PermissionRationalActivity::class.java)
                startActivityForResult(startIntent, 0)
            }
        }

        DetectActivitiesService.isServiceRunning.observe(
            this, {
                binding.startStopBtn.text = if (it) "Stop" else "Start"
            }
        )
    }

    private fun startStop() {
        Log.i(TAG, "startStop")
        if (!DetectActivitiesService.isServiceRunning.value!!) enableActivityRecognition() else disableActivityRecognition()
    }

    private fun enableActivityRecognition() {
        Log.i(TAG, "Enabling Recognition..")
        KillRequestFromMainActivity = false
        val intent = Intent(this@MainActivity, DetectActivitiesService::class.java)
        startForegroundService(intent)
    }

    private fun disableActivityRecognition() {
        Log.i(TAG, "Disabling Recognition..")
        KillRequestFromMainActivity = true
        val intent = Intent(this@MainActivity, DetectActivitiesService::class.java)
        stopService(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Start activity recognition if the permission was approved.
        if (appRequiredPermissionsApproved() && !DetectActivitiesService.isServiceRunning.value!!) {
            enableActivityRecognition()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun appRequiredPermissionsApproved(): Boolean {

        // Review permission check for 29+.
        return if (runningQOrLater) {
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) && PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        } else {
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
}