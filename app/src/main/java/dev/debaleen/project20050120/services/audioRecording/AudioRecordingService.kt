package dev.debaleen.project20050120.services.audioRecording

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.debaleen.project20050120.MainActivity
import dev.debaleen.project20050120.R
import dev.debaleen.project20050120.util.Constants
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AudioRecordingService : Service() {

    companion object {
        private val TAG = AudioRecordingService::class.java.simpleName
        private const val AudioRecordingNotificationId = 2
        private val isServiceRunning: MutableLiveData<Boolean> =
            MutableLiveData(false)
        val IsAudioRecordingServiceRunning: LiveData<Boolean>
            get() = isServiceRunning
    }

    private var recorder: MediaRecorder? = null

    private var mBinder: IBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        val serverInstance: AudioRecordingService
            get() = this@AudioRecordingService
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        val outputFile = File(
            getExternalFilesDir(null),
            "${SimpleDateFormat("dd-MM-yyyy HH-mm-ss", Locale.US).format(Date())}-audio.wav"
        )
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            setOutputFormat(MediaRecorder.OutputFormat.AMR_WB)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
            setAudioSamplingRate(Constants.AUDIO_SAMPLING_RATE)
            setOutputFile(outputFile)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        sendNotification()
        recorder?.apply {

            try {
                prepare()
                start()
                isServiceRunning.value = true
                Log.i(TAG, "prepare() & start() called.")
            } catch (e: Exception) {
                isServiceRunning.value = false
                Log.e(TAG, "prepare() & start() failed", e)
                Toast.makeText(
                    applicationContext,
                    "Couldn't start Audio recording.",
                    Toast.LENGTH_SHORT
                ).show()
                stopForeground(true)
            }
        }
        return START_STICKY
    }

    private fun sendNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(
            this, Constants.NotificationChannelId
        )
            .setContentTitle("Audio Recording Service is Running")
            .setContentText("Recording Background audio")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setGroup(Constants.NotificationGroup)
            .build()
        startForeground(AudioRecordingNotificationId, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        recorder?.apply {
            try {
                stop()
                reset()
                release()
                NotificationManagerCompat.from(this@AudioRecordingService).apply {
                    cancel(AudioRecordingNotificationId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "stop(), reset() & release() failed.", e)
            } finally {
                // since onDestroy() and recorder is being set to null, service will stop
                isServiceRunning.value = false
            }
        }
        recorder = null
    }
}