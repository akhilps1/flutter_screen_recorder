import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:hsb_screen_recorder/hsb_screen_recorder.dart';
import 'package:permission_handler/permission_handler.dart';

class PermissionHandler {
  static Future<PermissionHandler> initial() async {
    await Permission.storage.request();
    await Permission.notification.request();
    await Permission.photos.request();
    await Permission.microphone.request();

    return PermissionHandler();
  }
}

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await PermissionHandler.initial();
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _hsbScreenRecorderPlugin = HsbScreenRecorder();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback(
      (timeStamp) {},
    );
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> startScreenRecording() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      await _hsbScreenRecorderPlugin.startScreenRecording();
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;
  }

  Future<void> stopScreenRecording() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      await _hsbScreenRecorderPlugin.stopScreenRecording();
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
          backgroundColor: Colors.purple,
        ),
        body: Center(
          child: Column(
            children: [
              ElevatedButton(
                onPressed: startScreenRecording,
                child: Text('Start'),
              ),
              ElevatedButton(
                onPressed: stopScreenRecording,
                child: Text('Stop'),
              )
            ],
          ),
        ),
      ),
    );
  }
}
