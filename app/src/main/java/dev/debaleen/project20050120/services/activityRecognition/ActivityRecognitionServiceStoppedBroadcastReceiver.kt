package dev.debaleen.project20050120.services.activityRecognition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class ActivityRecognitionServiceStoppedBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private val TAG = ActivityRecognitionServiceStoppedBroadcastReceiver::class.java.simpleName
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive")

        if (context != null) {
            val workManager = WorkManager.getInstance(context)
            val startActivityRecServiceReq = OneTimeWorkRequest.Builder(
                ActivityRecognitionServiceStarterWorker::class.java
            ).build()
            workManager.enqueue(startActivityRecServiceReq)
        }
    }


}