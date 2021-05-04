package dev.debaleen.project20050120

import com.google.android.gms.location.DetectedActivity

class Constants {
    companion object {

        const val AppName = "Project20050120"

        const val DETECTION_INTERVAL_IN_MILLISECONDS: Long = 60000 // 1 minute interval

        const val ACTIVITY_CONFIDENCE_THRESHOLD = 70

        fun getActivityName(type: Int): String {
            return when (type) {
                DetectedActivity.IN_VEHICLE -> "IN_VEHICLE"
                DetectedActivity.ON_BICYCLE -> "ON_BICYCLE"
                DetectedActivity.ON_FOOT -> "ON_FOOT"
                DetectedActivity.RUNNING -> "RUNNING"
                DetectedActivity.STILL -> "STILL"
                DetectedActivity.TILTING -> "TILTING"
                DetectedActivity.WALKING -> "WALKING"
                DetectedActivity.UNKNOWN -> "UNKNOWN"
                else -> "UNCLASSIFIED"
            }
        }
    }
}