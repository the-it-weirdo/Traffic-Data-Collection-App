package dev.debaleen.project20050120.services.activityRecognition

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransitionRequest
import dev.debaleen.project20050120.util.Constants
import dev.debaleen.project20050120.MainActivity
import dev.debaleen.project20050120.R
import dev.debaleen.project20050120.services.activityRecognition.activityDetection.DetectedActivityBroadcastReceiver
import dev.debaleen.project20050120.services.activityRecognition.activityTransition.ActivityTransitionedBroadcastReceiver
import dev.debaleen.project20050120.util.FileLogger


class ActivitiesService : Service() {

    companion object {
        private val TAG = ActivitiesService::class.java.simpleName
        private const val ForegroundNotificationReqCode = 0
        private const val ActivityRecognitionNotificationId = 1
        private const val DetectedActivityReqCode = 1
        private const val ActivityTransitionedReqCode = 2
        private val isServiceRunning: MutableLiveData<Boolean> = MutableLiveData(false)
        val IsServiceRunning: LiveData<Boolean>
            get() = isServiceRunning
    }

    private lateinit var mIntentDetectedActivityBroadcastRcvr: Intent // for Activity Detection
    private lateinit var mIntentActivityTransitionedBroadcastRcvr: Intent // for Activity Transition

    private lateinit var mPendingIntentDetectedActivity: PendingIntent // for Activity Detection
    private lateinit var mPendingIntentActivityTransitioned: PendingIntent // for Activity Transition

    private lateinit var mActivityRecognitionClient: ActivityRecognitionClient
    private var mBinder: IBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        val serverInstance: ActivitiesService
            get() = this@ActivitiesService
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
        isServiceRunning.value = true

        val fileName = "${
            Constants.getCurrentDateTime("dd-MM-yyyy_HH-mm-ss")
        }_${Constants.ACTIVITY_RECOGNITION_FILE}"
        val logger = FileLogger(fileName, false)

        mActivityRecognitionClient = ActivityRecognitionClient(this)

        mIntentDetectedActivityBroadcastRcvr =
            Intent(this, DetectedActivityBroadcastReceiver::class.java)
        mIntentDetectedActivityBroadcastRcvr.putExtra(Constants.ACTIVITY_RECOGNITION_FILE, fileName)

        mIntentActivityTransitionedBroadcastRcvr =
            Intent(this, ActivityTransitionedBroadcastReceiver::class.java)

        mPendingIntentDetectedActivity =
            PendingIntent.getBroadcast(
                this, DetectedActivityReqCode, mIntentDetectedActivityBroadcastRcvr,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        mPendingIntentActivityTransitioned = PendingIntent.getBroadcast(
            this, ActivityTransitionedReqCode, mIntentActivityTransitionedBroadcastRcvr,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        try {
            logger.writeToFile("TIMESTAMP,ACTIVITY,CONFIDENCE\n")
        } catch (e: Exception) {
            Log.e(TAG, "Activity File creation failed", e)
        }

        requestActivityDetectionUpdates()
        requestActivityTransitionUpdates()
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
            this, ForegroundNotificationReqCode, notificationIntent, 0
        )
        val notification: Notification =
            NotificationCompat.Builder(this, Constants.NotificationChannelId)
                .setContentTitle("Service is Running")
                .setContentText("Listening for Activity Recognition events")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setGroup(Constants.NotificationGroup)
                .build()
        startForeground(ActivityRecognitionNotificationId, notification)
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

    private fun requestActivityTransitionUpdates() {
        val task = mActivityRecognitionClient.requestActivityTransitionUpdates(
            ActivityTransitionRequest(Constants.ActivityTransitionList),
            mPendingIntentActivityTransitioned
        )

        task.addOnCompleteListener {
            Log.i(
                TAG,
                "Activity Transition Update Request task completed. Successful: ${it.isSuccessful}"
            )
            Toast.makeText(
                applicationContext,
                "Activity Transition Update Request task completed. Successful: ${it.isSuccessful}",
                Toast.LENGTH_SHORT
            ).show()
        }
        task.addOnSuccessListener {
            Log.i(TAG, "Successfully requested activity transition updates")

            sendNotification()

            Toast.makeText(
                applicationContext,
                "Successfully requested activity transition updates",
                Toast.LENGTH_SHORT
            ).show()
        }
        task.addOnFailureListener {
            Log.i(TAG, "Requesting activity transition updates failed to start. $it")
            Toast.makeText(
                applicationContext,
                "Requesting activity transition updates failed to start. $it",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun removeActivityTransitionUpdates() {
        val task = mActivityRecognitionClient.removeActivityTransitionUpdates(
            mPendingIntentActivityTransitioned
        )
        task.addOnCompleteListener {
            Log.i(
                TAG,
                "Stop Activity Transition updates task completed. Successful: ${it.isSuccessful}"
            )
            Toast.makeText(
                applicationContext,
                "Stop Activity Transition updates task completed. Successful: ${it.isSuccessful}",
                Toast.LENGTH_SHORT
            ).show()
        }
        task.addOnSuccessListener {
            Log.i(TAG, "Stop Activity Transition updates successful!")
            Toast.makeText(
                applicationContext,
                "Stop Activity Transition updates successful!",
                Toast.LENGTH_SHORT
            ).show()
        }
        task.addOnFailureListener {
            Log.i(TAG, "Failed to remove Activity Transition updates! $it")
            Toast.makeText(
                applicationContext, "Failed to remove Activity Transition updates! $it",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning.value = false
        removeActivityDetectionUpdates()
        removeActivityTransitionUpdates()
        if (!MainActivity.KillRequestFromMainActivity) {
            // If the service is not killed from MainActivity, send broadcast to restart it.
            val intent =
                Intent(this, ActivityRecognitionServiceStoppedBroadcastReceiver::class.java)
            sendBroadcast(intent)
        }
    }
}