package dev.debaleen.project20050120.activityRecognition.activityTransition

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.ActivityTransitionResult
import dev.debaleen.project20050120.util.Constants
import dev.debaleen.project20050120.R
import dev.debaleen.project20050120.StopInformationFormActivity

class ActivityTransitionedIntentService : JobIntentService() {

    companion object {
        const val CHANNEL_ID_NAME = "Activity Transition Updates"
        private val TAG = ActivityTransitionedIntentService::class.java.simpleName
        private const val ActivityRecognitionUpdateReqCode = 4
        private const val ActivityRecognitionNotificationId = 4
        fun enqueueWork(context: Context, intent: Intent) {
            Log.i(TAG, "enqueueWork")
            enqueueWork(context, ActivityTransitionedIntentService::class.java, 2, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        Log.i(TAG, "onHandleWork")
        val result = ActivityTransitionResult.extractResult(intent)
        val detectedTransitions = result?.transitionEvents
        if (detectedTransitions != null) {
            val strBuffer = StringBuffer()
            for (transitionEvent in detectedTransitions) {
                strBuffer.append(
                    "${Constants.getActivityName(transitionEvent.activityType)} : ${
                        Constants.getActivityTransitionName(
                            transitionEvent.transitionType
                        )
                    }\n"
                )
            }
            sendNotification(strBuffer.toString())
        }

    }

    private fun sendNotification(contentText: String) {
        val notificationIntent = Intent(this, StopInformationFormActivity::class.java)
        /*
           flag = 0 means no flag in PendingIntent.
           Setting no flags, i.e. 0 as the flags parameter, is to return an existing PendingIntent
           if there is one that matches the parameters provided.
           If there is no existing matching PendingIntent then a new one will be created and returned
        */
        val pendingIntent = PendingIntent.getActivity(
            this, ActivityRecognitionUpdateReqCode, notificationIntent, 0
        )
        val notificationBuilder = NotificationCompat.Builder(
            this, CHANNEL_ID_NAME
        )
            .setContentTitle("Activity Transition Update")
            .setContentText(contentText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).apply {
            notify(ActivityRecognitionNotificationId, notificationBuilder.build())
        }
    }
}