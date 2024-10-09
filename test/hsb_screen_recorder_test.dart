import 'package:flutter_test/flutter_test.dart';
import 'package:hsb_screen_recorder/hsb_screen_recorder.dart';
import 'package:hsb_screen_recorder/hsb_screen_recorder_platform_interface.dart';
import 'package:hsb_screen_recorder/hsb_screen_recorder_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockHsbScreenRecorderPlatform
    with MockPlatformInterfaceMixin
    implements HsbScreenRecorderPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final HsbScreenRecorderPlatform initialPlatform = HsbScreenRecorderPlatform.instance;

  test('$MethodChannelHsbScreenRecorder is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelHsbScreenRecorder>());
  });

  test('getPlatformVersion', () async {
    HsbScreenRecorder hsbScreenRecorderPlugin = HsbScreenRecorder();
    MockHsbScreenRecorderPlatform fakePlatform = MockHsbScreenRecorderPlatform();
    HsbScreenRecorderPlatform.instance = fakePlatform;

    expect(await hsbScreenRecorderPlugin.getPlatformVersion(), '42');
  });
}
