package dev.debaleen.project20050120

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MediatorLiveData
import dev.debaleen.project20050120.activityRecognition.ActivitiesService
import dev.debaleen.project20050120.activityRecognition.activityTransition.ActivityTransitionedIntentService
import dev.debaleen.project20050120.audioRecording.AudioRecordingService
import dev.debaleen.project20050120.databinding.ActivityMainBinding
import dev.debaleen.project20050120.util.Constants
import dev.debaleen.project20050120.util.appRequiredPermissionsApproved

class MainActivity : AppCompatActivity() {

    companion object {
        var KillRequestFromMainActivity: Boolean = false
        private val TAG = MainActivity::class.java.simpleName
    }

    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var isServicesRunning: MediatorLiveData<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        isServicesRunning = MediatorLiveData()

        mBinding.startStopBtn.setOnClickListener {
            // Request permission and start activity for result. If the permission is approved, we
            // want to make sure we start activity recognition tracking.
            if (appRequiredPermissionsApproved(runningQOrLater)) {
                startStop()
            } else {
                val startIntent = Intent(this, PermissionRationalActivity::class.java)
                startActivityForResult(startIntent, 0)
            }
        }

        isServicesRunning.addSource(ActivitiesService.IsServiceRunning) {
            isServicesRunning.value = checkIfServicesRunning()
        }
        isServicesRunning.addSource(AudioRecordingService.IsAudioRecordingServiceRunning) {
            isServicesRunning.value = checkIfServicesRunning()
        }

        isServicesRunning.observe(
            this, {
                mBinding.startStopBtn.text = if (it) "Stop" else "Start"
            }
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val serviceChannel = NotificationChannel(
                Constants.NotificationChannelId,
                Constants.AppName,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val activityTransitionUpdateChannel = NotificationChannel(
                ActivityTransitionedIntentService.CHANNEL_ID_NAME, //Id
                ActivityTransitionedIntentService.CHANNEL_ID_NAME, //Name
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
            }


            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
            manager.createNotificationChannel(activityTransitionUpdateChannel)
        }
    }

    private fun sendGroupNotification() {
        val groupNotification =
            NotificationCompat.Builder(this@MainActivity, Constants.NotificationChannelId)
                .setContentTitle("Project 20050120")
                //set content text to support devices running API level < 24
                .setContentText("Services Running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setGroup(Constants.NotificationGroup)
                .setGroupSummary(true)
                .setOnlyAlertOnce(true)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                .build()

        NotificationManagerCompat.from(this).apply {
            notify(Constants.GroupNotificationsId, groupNotification)
        }
    }

    private fun cancelGroupNotification() {
        NotificationManagerCompat.from(this).apply {
            cancel(Constants.GroupNotificationsId)
        }
    }

    private fun startStop() {
        Log.i(TAG, "startStop")
        if (!checkIfServicesRunning()) enableServices() else disableServices()
    }

    private fun checkIfServicesRunning(): Boolean {
        return (ActivitiesService.IsServiceRunning.value!!
                || AudioRecordingService.IsAudioRecordingServiceRunning.value!!)
    }

    private fun enableServices() {
        Log.i(TAG, "Enabling Services..")
        KillRequestFromMainActivity = false

        // Activity Recognition
        if (!ActivitiesService.IsServiceRunning.value!!) {
            val intent = Intent(this@MainActivity, ActivitiesService::class.java)
            startForegroundService(intent)
        }

        // Audio Recording
        if (!AudioRecordingService.IsAudioRecordingServiceRunning.value!!) {
            val intent = Intent(this@MainActivity, AudioRecordingService::class.java)
            startForegroundService(intent)
        }

        sendGroupNotification()
    }

    private fun disableServices() {
        Log.i(TAG, "Disabling Services..")
        KillRequestFromMainActivity = true

        // Activity Recognition
        if (ActivitiesService.IsServiceRunning.value!!) {
            val activityRecognitionIntent = Intent(this@MainActivity, ActivitiesService::class.java)
            stopService(activityRecognitionIntent)
        }

        // Audio Recording
        if (AudioRecordingService.IsAudioRecordingServiceRunning.value!!) {
            val audioRecordingIntent = Intent(this@MainActivity, AudioRecordingService::class.java)
            stopService(audioRecordingIntent)
        }

        cancelGroupNotification()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Start activity recognition if the permission was approved.
        if (appRequiredPermissionsApproved(runningQOrLater) && !checkIfServicesRunning()) {
            enableServices()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}