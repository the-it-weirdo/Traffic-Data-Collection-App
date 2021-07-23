package dev.debaleen.project20050120.services.activityRecognition.activityDetection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DetectedActivityBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private val TAG = DetectedActivityBroadcastReceiver::class.java.simpleName
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            Log.i(TAG, "onReceive: Broadcast received in DetectedActivityBroadcastReceiver")
//            val intent = Intent(context, DetectedActivityIntentService::class.java)
            DetectedActivityIntentService.enqueueWork(context, intent)
        }
    }
}