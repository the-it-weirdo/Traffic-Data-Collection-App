package dev.debaleen.project20050120.util

import android.Manifest
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.DetectedActivity

class Constants {
    companion object {

        const val AppName = "Project20050120"

        const val BASE_DIR = "Data Crowd Sourcing"

        const val DETECTION_INTERVAL_IN_MILLISECONDS: Long = 60000 // 1 minute interval

        const val AUDIO_SAMPLING_RATE = 44100

        const val NotificationChannelId = "dev.debaleen.project20050120"
        const val GroupNotificationsId = 20050120
        const val NotificationGroup = "Project 20050120"

        const val ACTIVITY_CONFIDENCE_THRESHOLD = 70

        const val PopupNotificationActivity =
            DetectedActivity.WALKING // change to IN_VEHICLE before release

        const val PopupNotificationActivityTransition = ActivityTransition.ACTIVITY_TRANSITION_EXIT

        val ActivityTransitionList = listOf<ActivityTransition>(
            ActivityTransition.Builder().setActivityType(PopupNotificationActivity)
                .setActivityTransition(PopupNotificationActivityTransition).build()
        )

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

        fun getActivityTransitionName(type: Int): String {
            return when (type) {
                ActivityTransition.ACTIVITY_TRANSITION_ENTER -> "Enter"
                ActivityTransition.ACTIVITY_TRANSITION_EXIT -> "Exit"
                else -> "UNKNOWN"
            }
        }
    }
}

enum class VehicleType {
    BUS, CAB, TWO_WHEELER, THREE_WHEELER, FOUR_WHEELER, TWO_WHEELER_WITH_ENGINE, THREE_WHEELER_WITH_ENGINE
}

enum class BusStopType {
    ADHOC, CONGESTION, SIGNAL, BUS_STOP
}