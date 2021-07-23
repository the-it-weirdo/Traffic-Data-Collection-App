package dev.debaleen.project20050120.services.activityRecognition.activityTransition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ActivityTransitionedBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private val TAG = ActivityTransitionedIntentService::class.java.simpleName
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            Log.i(TAG, "ActivityTransitionedBroadcast received.")
            ActivityTransitionedIntentService.enqueueWork(context, intent)
        }
    }
}