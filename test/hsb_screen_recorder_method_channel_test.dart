import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:hsb_screen_recorder/hsb_screen_recorder_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelHsbScreenRecorder platform = MethodChannelHsbScreenRecorder();
  const MethodChannel channel = MethodChannel('hsb_screen_recorder');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
