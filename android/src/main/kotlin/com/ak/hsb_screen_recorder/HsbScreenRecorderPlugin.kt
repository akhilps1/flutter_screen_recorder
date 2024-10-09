package com.ak.hsb_screen_recorder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

/** HsbScreenRecorderPlugin */
class HsbScreenRecorderPlugin :
        FlutterPlugin, PluginRegistry.ActivityResultListener, MethodCallHandler, ActivityAware {
  private lateinit var channel: MethodChannel
  private var activityBinding: ActivityPluginBinding? = null
  private var context: Context? = null
  private var activity: Activity? = null

  val REQUEST_CODE = 3333 // Adjust as needed

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    context = flutterPluginBinding.getApplicationContext()

    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "hsb_screen_recorder")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(call: MethodCall, result: Result) {

    when (call.method) {
      "startScreenRecording" -> startScreenRecording(call, result)
      "stopScreenRecording" -> stopScreenRecording(result)
      else -> result.notImplemented()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    println("onActivityResult requestCode: $requestCode, resultCode: $resultCode")
    startScreenRecordingService(-1, data)
    if (requestCode == REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        // Permission granted, start screen recording service
        println("Permission granted, starting screen recording service")
        startScreenRecordingService(resultCode, data)
        return true
      } else {
        // Permission denied, handle the situation appropriately
        println("Permission denied")
        return false
      }
    }
    return false
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activityBinding = binding
    activity = binding.getActivity()
    activityBinding!!.addActivityResultListener(this)
  }

  override fun onDetachedFromActivity() {
    activityBinding = null // Clear activity reference
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activityBinding = binding // Reassign activity reference
    activity = binding.getActivity()
    activityBinding!!.addActivityResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    activityBinding = null // Clear activity reference
    activity = null
  }

  private fun startScreenRecording(call: MethodCall, result: Result) {
    println("startScreenRecording()")

    val projectionManager =
            activityBinding!!.activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as
                    MediaProjectionManager
    val mediaProjectionIntent = projectionManager.createScreenCaptureIntent()
    activity!!.startActivityForResult(mediaProjectionIntent, REQUEST_CODE, null)

    result.success(true)
  }

  private fun startScreenRecordingService(resultCode: Int, data: Intent?) {
    println("activity $activity")

    val serviceIntent =
            Intent(activity!!, ScreenRecordingService::class.java).apply {
              action = ScreenRecordingService.ACTION_START
              putExtra("RESULT_CODE", resultCode)
              putExtra("DATA", data)
            }

    activity!!.startService(serviceIntent)
  }

  private fun stopScreenRecording(result: Result) {
    val serviceIntent =
            Intent(activity!!, ScreenRecordingService::class.java).apply {
              action = ScreenRecordingService.ACTION_STOP
            }
    activity!!.startService(serviceIntent)
    result.success(null)
  }
}
