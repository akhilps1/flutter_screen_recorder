import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'hsb_screen_recorder_method_channel.dart';

abstract class HsbScreenRecorderPlatform extends PlatformInterface {
  /// Constructs a HsbScreenRecorderPlatform.
  HsbScreenRecorderPlatform() : super(token: _token);

  static final Object _token = Object();

  static HsbScreenRecorderPlatform _instance = MethodChannelHsbScreenRecorder();

  /// The default instance of [HsbScreenRecorderPlatform] to use.
  ///
  /// Defaults to [MethodChannelHsbScreenRecorder].
  static HsbScreenRecorderPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [HsbScreenRecorderPlatform] when
  /// they register themselves.
  static set instance(HsbScreenRecorderPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<bool> startScreenRecording() {
    throw UnimplementedError(
        'startScreenRecording() has not been implemented.');
  }

  Future<bool> stopScreenRecording() {
    throw UnimplementedError('stopScreenRecording() has not been implemented.');
  }
}
