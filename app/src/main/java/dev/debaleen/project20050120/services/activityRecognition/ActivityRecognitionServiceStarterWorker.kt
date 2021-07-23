package dev.debaleen.project20050120.services.activityRecognition

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class ActivityRecognitionServiceStarterWorker(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {
        private val TAG = ActivityRecognitionServiceStoppedBroadcastReceiver::class.java.simpleName
    }

    override fun doWork(): Result {
        Log.d(TAG, "doWork $id")
        Log.d(TAG, "Service running: ${ActivitiesService.IsServiceRunning.value}")
        if (!ActivitiesService.IsServiceRunning.value!!) {
            Log.d(TAG, "Starting ActivitiesService")
            val intent = Intent(context, ActivitiesService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
        return Result.success()
    }

    override fun onStopped() {
        Log.d(TAG, "onStop for $id")
        super.onStopped()
    }
}