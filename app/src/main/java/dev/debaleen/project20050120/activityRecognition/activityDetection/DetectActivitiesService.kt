package dev.debaleen.project20050120.activityRecognition.activityDetection

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.ActivityRecognitionClient
import dev.debaleen.project20050120.Constants
import dev.debaleen.project20050120.MainActivity
import dev.debaleen.project20050120.R
import dev.debaleen.project20050120.activityRecognition.ActivityRecognitionServiceStoppedBroadcastReceiver


class DetectActivitiesService : Service() {

    companion object {
        private val TAG = DetectActivitiesService::class.java.simpleName
        private const val CHANNEL_ID = "Activity Recognition"
        private val _isServiceRunning: MutableLiveData<Boolean> = MutableLiveData(false)
        val isServiceRunning: LiveData<Boolean>
            get() = _isServiceRunning
    }

    private lateinit var mIntentDetectedActivityService: Intent
    private lateinit var mPendingIntentDetectedActivity: PendingIntent
    private lateinit var mActivityRecognitionClient: ActivityRecognitionClient
    private var mBinder: IBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        val serverInstance: DetectActivitiesService
            get() = this@DetectActivitiesService
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
        createNotificationChannel()
        mActivityRecognitionClient = ActivityRecognitionClient(this)
        mIntentDetectedActivityService = Intent(this, DetectedActivityIntentService::class.java)
        mPendingIntentDetectedActivity =
            PendingIntent.getService(
                this,
                1,
                mIntentDetectedActivityService,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        requestActivityDetectionUpdates()
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i(TAG, "onBind")
        return mBinder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.i(TAG, "Start Command")
        /*
          when the phone runs out of memory and kills the service before it finishes executing,
          START_STICKY tells the OS to recreate the service after it has enough memory and call
          onStartCommand() again with a null intent.
        */
        return START_STICKY
    }

    private fun sendNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        /*
           flag = 0 means no flag in PendingIntent.
           Setting no flags, i.e. 0 as the flags parameter, is to return an existing PendingIntent
           if there is one that matches the parameters provided.
           If there is no existing matching PendingIntent then a new one will be created and returned
        */
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Service is Running")
            .setContentText("Listening for Activity Recognition events")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appName = Constants.AppName
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                appName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun requestActivityDetectionUpdates() {
        val task = mActivityRecognitionClient.requestActivityUpdates(
            Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
            mPendingIntentDetectedActivity
        )
        task.addOnCompleteListener {
            Log.i(TAG, "Start task completed. Successful: ${it.isSuccessful}")
            Toast.makeText(
                applicationContext,
                "Start task completed. Successful: ${it.isSuccessful}",
                Toast.LENGTH_SHORT
            ).show()
        }
        task.addOnSuccessListener {
            Log.i(TAG, "Successfully requested activity updates")

            _isServiceRunning.value = true
            sendNotification()

            Toast.makeText(
                applicationContext,
                "Successfully requested activity updates",
                Toast.LENGTH_SHORT
            ).show()
        }
        task.addOnFailureListener {
            Log.i(TAG, "Requesting activity updates failed to start. $it")
            Toast.makeText(
                applicationContext,
                "Requesting activity updates failed to start. $it",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun removeActivityDetectionUpdates() {
        val task = mActivityRecognitionClient.removeActivityUpdates(
            mPendingIntentDetectedActivity
        )
        task.addOnCompleteListener {
            Log.i(TAG, "Stop task completed. Successful: ${it.isSuccessful}")
            Toast.makeText(
                applicationContext,
                "Stop task completed. Successful: ${it.isSuccessful}",
                Toast.LENGTH_SHORT
            ).show()
        }
        task.addOnSuccessListener {
            Log.i(TAG, "Removed activity updates successfully!")
            Toast.makeText(
                applicationContext,
                "Removed activity updates successfully!",
                Toast.LENGTH_SHORT
            ).show()
        }
        task.addOnFailureListener {
            Log.i(TAG, "Failed to remove activity updates! $it")
            Toast.makeText(
                applicationContext, "Failed to remove activity updates! $it",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _isServiceRunning.value = false
        removeActivityDetectionUpdates()
        if (!MainActivity.KillRequestFromMainActivity) {
            // If the service is not killed from MainActivity, send broadcast to restart it.
            val intent =
                Intent(this, ActivityRecognitionServiceStoppedBroadcastReceiver::class.java)
            sendBroadcast(intent)
        }
    }
}