import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'hsb_screen_recorder_platform_interface.dart';

/// An implementation of [HsbScreenRecorderPlatform] that uses method channels.
class MethodChannelHsbScreenRecorder extends HsbScreenRecorderPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('hsb_screen_recorder');

  @override
  Future<bool> startScreenRecording() async {
    await methodChannel.invokeMethod('startScreenRecording');

    return true;
  }

  @override
  Future<bool> stopScreenRecording() async {
    await methodChannel.invokeMethod('stopScreenRecording');
    return true;
  }
}
