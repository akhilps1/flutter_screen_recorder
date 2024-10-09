package com.ak.hsb_screen_recorder

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.IOException

class ScreenRecordingService : Service() {

    private var mediaProjection: MediaProjection? = null
    private var mediaRecorder: MediaRecorder? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var mediaProjectionCallback: MediaProjectionCallback? = null

    companion object {
        const val CHANNEL_ID = "ScreenRecordingServiceChannel"
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                // Start screen recording
                startRecording(intent)
            }
            ACTION_STOP -> {
                // Stop screen recording
                stopRecording()
            }
        }
        return START_STICKY
    }

    private fun startRecording(intent: Intent) {
        try {
            Log.d("startRecording", "Starting Screen Recording")
            createNotification()
            val resultCode = intent.getIntExtra("RESULT_CODE", Activity.RESULT_OK)
            val data =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra("DATA", Intent::class.java)
                    } else {
                        intent.getParcelableExtra<Intent>("DATA")
                    }

            val projectionManager =
                    getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

            mediaProjectionCallback = MediaProjectionCallback()
            mediaProjection = projectionManager.getMediaProjection(resultCode, data!!)
            mediaProjection?.registerCallback(mediaProjectionCallback!!, null)

            setupMediaRecorder()
            createVirtualDisplay()

            mediaRecorder?.start()
        } catch (e: Exception) {
            Log.e("ScreenRecordingService", "Error starting recording: ${e.message}")
            stopRecording() // Stop recording if it fails
        }
    }

    private fun stopRecording() {
        Log.d("stopRecording", "Stopping Screen Recording ${mediaRecorder != null}")
        try {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaProjection?.stop()
            virtualDisplay?.release()
        } catch (e: Exception) {
            Log.e("ScreenRecordingService", "Error stopping recording: ${e.message}")
        }

      

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_DETACH)
        } else {
            stopForeground(true)
        }
        // stopSelf()
    }

    private fun setupMediaRecorder() {

        try {
        Log.d("Path", "${externalCacheDir?.absolutePath}")
        mediaRecorder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(this).apply {
                        setVideoSource(MediaRecorder.VideoSource.SURFACE)
                        // setAudioSource(MediaRecorder.AudioSource.MIC)
                        // setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                        // setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                        setOutputFile("${externalCacheDir?.absolutePath}/screen_recording.mp4")
                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        setVideoSize(1080, 1920) // Set your resolution here
                        setVideoFrameRate(60) // FPS
                        setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                        setVideoEncodingBitRate(5 * 1024 * 1024)
                        prepare()
                    }
                } else {
                    MediaRecorder().apply {
                        setVideoSource(MediaRecorder.VideoSource.SURFACE)
                        // setAudioSource(MediaRecorder.AudioSource.MIC)
                        // setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                        // setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                        setOutputFile("${externalCacheDir?.absolutePath}/screen_recording.mp4")
                        setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                        setVideoSize(1080, 1920) // Set your resolution here
                        setVideoFrameRate(60) // FPS
                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        setVideoEncodingBitRate(5 * 1024 * 1024)
                        prepare()
                    }
                }

            } catch (e: IOException) {
                Log.d("--INIT-RECORDER", e.message + "")
                println("Error startRecordScreen")
                println(e.message)
            }
    }

    private fun createVirtualDisplay() {
        try {
        val metrics = resources.displayMetrics
        virtualDisplay =
                mediaProjection?.createVirtualDisplay(
                        "ScreenRecording",
                        metrics.widthPixels,
                        metrics.heightPixels,
                        metrics.densityDpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        mediaRecorder?.surface,
                        null,
                        null
                )
        } catch (e: Exception) {
            println("createVirtualDisplay err")
            println(e.message)
        
        }
      
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                    NotificationChannel(
                            CHANNEL_ID,
                            "Screen Recording",
                            NotificationManager.IMPORTANCE_DEFAULT
                    )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification =
                NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Screen Recording")
                        .setContentText("Recording your screen")
                        // .setSmallIcon(R.drawable.icon) // Set your icon here
                        .build()

        startForeground(1, notification)
    }

    inner class MediaProjectionCallback : MediaProjection.Callback() {
        override fun onStop() {
            mediaRecorder?.reset()
            mediaProjection = null
            stopScreenSharing()
        }
    }

    private fun stopScreenSharing() {
        if (virtualDisplay != null) {
            virtualDisplay!!.release()
            if (mediaProjection != null && mediaProjectionCallback != null) {
                mediaProjection!!.unregisterCallback(mediaProjectionCallback!!)
                mediaProjection?.stop()
                mediaProjection = null
                Log.d("TAG", "unregisterCallback")
            }
            Log.d("TAG", "MediaProjection Stopped")
        }
    }
}
