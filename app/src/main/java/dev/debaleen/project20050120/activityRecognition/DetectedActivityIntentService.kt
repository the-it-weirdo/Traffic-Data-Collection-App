package dev.debaleen.project20050120.activityRecognition

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import dev.debaleen.project20050120.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class DetectedActivityIntentService : IntentService(TAG) {

    companion object {
        private val TAG = DetectedActivityIntentService::class.java.simpleName
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.i(TAG, "onHandleIntent")
        if (intent != null) {
            val result = ActivityRecognitionResult.extractResult(intent)
            // Get the list of the probable activities associated with the current state of the
            // device. Each activity is associated with a confidence level, which is an int between
            // 0 and 100.
            val detectedActivities = result?.probableActivities as ArrayList<DetectedActivity>
            for (activity in detectedActivities) {
//                broadcastActivity(activity)
                writeToLog(activity)
            }
        }
    }

//    private fun broadcastActivity(activity: DetectedActivity) {
//        Log.i(TAG, "Broadcasting $activity")
//        val intent = Intent(MainActivity.BROADCAST_DETECTED_ACTIVITY)
//        intent.putExtra("type", activity.type)
//        intent.putExtra("confidence", activity.confidence)
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//    }

    private fun writeToLog(activity: DetectedActivity) {
        if (activity.confidence > Constants.ACTIVITY_CONFIDENCE_THRESHOLD) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.i(TAG, "Writing to log..")
                    Log.i(TAG, getExternalFilesDir(null).toString())
                    Log.i(TAG, "${SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(Date())}, ${
                        Constants.getActivityName(
                            activity.type
                        )
                    }, ${activity.confidence}\n")
                    val file = File(getExternalFilesDir(null), "ActivityRecognitionLog.txt")
                    file.appendText(
                        "${SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(Date())}, ${
                            Constants.getActivityName(
                                activity.type
                            )
                        }, ${activity.confidence}\n"
                    )
                } catch (e: IOException) {
                    Log.e("Exception", "File write failed: $e")
                }
            }
        }
    }
}