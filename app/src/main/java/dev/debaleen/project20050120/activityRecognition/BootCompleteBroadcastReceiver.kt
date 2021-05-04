package dev.debaleen.project20050120.activityRecognition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class BootCompleteBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private val TAG = BootCompleteBroadcastReceiver::class.java.simpleName
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action != Intent.ACTION_BOOT_COMPLETED && intent?.action != Intent.ACTION_LOCKED_BOOT_COMPLETED)
            return

        Log.d(TAG, "Boot complete received. Starting Activity Recognition Service...")
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