import 'hsb_screen_recorder_platform_interface.dart';

class HsbScreenRecorder {
  Future<bool> startScreenRecording() {
    return HsbScreenRecorderPlatform.instance.startScreenRecording();
  }

  Future<bool> stopScreenRecording() {
    return HsbScreenRecorderPlatform.instance.stopScreenRecording();
  }
}
