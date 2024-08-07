package dev.debaleen.project20050120.services.activityRecognition.activityDetection

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import dev.debaleen.project20050120.util.Constants
import dev.debaleen.project20050120.util.FileLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*


class DetectedActivityIntentService : JobIntentService() {

    companion object {
        private val TAG = DetectedActivityIntentService::class.java.simpleName
        fun enqueueWork(context: Context, intent: Intent) {
            Log.i(TAG, "enqueueWork")
            enqueueWork(context, DetectedActivityIntentService::class.java, 1, intent)
        }
    }

    private var logger: FileLogger? = null

    override fun onHandleWork(intent: Intent) {
        Log.i(TAG, "onHandleWork")
        val result = ActivityRecognitionResult.extractResult(intent)
        val fileName = intent.getStringExtra(Constants.ACTIVITY_RECOGNITION_FILE)
        if (fileName != null) {
            logger = FileLogger(fileName, false)
        }
        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        val detectedActivities = result?.probableActivities as ArrayList<DetectedActivity>
        for (activity in detectedActivities)
            writeToLog(activity)

    }

    private fun writeToLog(activity: DetectedActivity) {
        if (activity.confidence > Constants.ACTIVITY_CONFIDENCE_THRESHOLD) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.i(TAG, "Writing to log..")
                    Log.i(
                        TAG,
                        "${Constants.getCurrentDateTime()},${
                            Constants.getActivityName(
                                activity.type
                            )
                        },${activity.confidence}\n"
                    )
                    if (logger == null) {
                        Log.e(TAG, "Logger is null")
                    }
                    logger?.writeToFile(
                        "${Constants.getCurrentDateTime()}," +
                                "${Constants.getActivityName(activity.type)}," +
                                "${activity.confidence}\n"
                    )
                } catch (e: IOException) {
                    Log.e("Exception", "File write failed: $e")
                }
            }
        }
    }
}