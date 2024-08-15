import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:screenshot/screenshot.dart';
import 'package:scribble_etome_plugin_plus/canvas.dart';
import 'package:scribble_etome_plugin_plus/controller.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:image_picker/image_picker.dart';
import 'package:flutter_image_compress/flutter_image_compress.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await requestStoragePermission();
  SystemChrome.setEnabledSystemUIMode(SystemUiMode.manual, overlays: []);
  runApp(const MyApp());
}

Future<void> requestStoragePermission() async {
  var status = await Permission.storage.status;
  if (!status.isGranted) {
    await Permission.storage.request();
  }
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: HomeScreen(),
    );
  }
}

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  ScreenshotController screenshotController = ScreenshotController();

  @override
  void initState() {
    _loadCanvas();
    super.initState();
  }

  _loadCanvas() async {
    String filePath = '/storage/emulated/0/Documents/1.png';
    File file = File(filePath);
    if (await file.exists()) {
      CanvasController.load('1');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // appBar: AppBar(
      //   toolbarHeight: 45,
      //   title:
      // ),
      body: Stack(
        children: [
          Screenshot(
            controller: screenshotController,
            child: const CanvasEtome(
              saveFolder: 'Documents',
            ),
          ),
          Positioned(
            top: 0,
            child: buildContainer(),
          )
          // Container(
          //   height: 45,
          //   color: Colors.green,
          // ),
        ],
      ),
    );
  }

  void _showAlertDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text('Alert Dialog Test'),
          content: const Text('This is a test alert dialog.'),
          actions: <Widget>[
            TextButton(
              child: const Text('OK'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }

  Container buildContainer() {
    return Container(
      decoration: const BoxDecoration(
        border: Border(
          bottom: BorderSide(
            width: 1,
            color: Colors.black,
          ),
        ),
      ),
      child: Row(
        children: [
          Container(
            height: 45,
            decoration: const BoxDecoration(
              border: Border(
                bottom: BorderSide(
                  width: 1,
                  color: Colors.black,
                ),
              ),
            ),
            child: Row(
              children: [
                IconButton(
                  onPressed: () {
                    CanvasController.clear();
                  },
                  icon: const Icon(Icons.clear),
                ),
                IconButton(
                  onPressed: () {
                    CanvasController.undo();
                  },
                  icon: const Icon(Icons.undo),
                ),
                IconButton(
                  onPressed: () {
                    CanvasController.redo();
                  },
                  icon: const Icon(Icons.redo),
                ),
                IconButton(
                  onPressed: () {
                    CanvasController.setDrawingTool(DrawingTool.fountainPen);
                  },
                  icon: const Icon(Icons.window_outlined),
                ),
                IconButton(
                  onPressed: () {
                    CanvasController.rotateImageLeft();
                  },
                  icon: const Icon(Icons.rotate_left),
                ),
                IconButton(
                  onPressed: () {
                    CanvasController.rotateImageRight();
                  },
                  icon: const Icon(Icons.rotate_right),
                ),
                IconButton(
                  onPressed: () {
                    CanvasController.increaseImageSize();
                  },
                  icon: const Icon(Icons.add),
                ),
                IconButton(
                  onPressed: () {
                    CanvasController.decreaseImageSize();
                  },
                  icon: const Icon(Icons.remove),
                ),

                // IconButton(
                //   onPressed: () async {
                //     CanvasController.refreshCurrentView();
                //   },
                //   icon: const Icon(Icons.abc_outlined),
                // ),
                IconButton(
                  onPressed: () async {
                    String? base64String =
                        await pickImageAndConvertToBase64(context);
                    if (base64String != null) {
                      CanvasController.addImage(base64String, top: 0);
                    }
                  },
                  icon: const Icon(
                    Icons.warning_amber_outlined,
                    color: Colors.red,
                  ),
                ),
                IconButton(
                  onPressed: () async {
                    try {
                      File file = File('/storage/emulated/0/Documents/2.png');
                      if (await file.exists()) file.delete();
                      // Uint8List? bytes = await screenshotController.capture();
                      Uint8List? bytes =
                          await CanvasController.getWholeBitmap();
                      await file.writeAsBytes(bytes);
                      CanvasController.clear();
                      Fluttertoast.showToast(msg: "Saved successfully");
                    } on Exception catch (e) {
                      Fluttertoast.showToast(msg: "Failed $e");
                    }
                  },
                  icon: const Icon(Icons.save),
                ),
                IconButton(
                  onPressed: () async {
                    try {
                      File file = File('/storage/emulated/0/Documents/2.png');
                      if (!await file.exists()) return;
                      await Future.delayed(
                        const Duration(milliseconds: 600),
                        () {
                          CanvasController.load('2');
                        },
                      );
                    } on Exception catch (e) {
                      Fluttertoast.showToast(msg: "Failed $e");
                    }
                  },
                  icon: const Icon(Icons.download),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Future<String?> pickImageAndConvertToBase64(BuildContext context) async {
    ImagePicker picker = ImagePicker();
    final pickedFile = await picker.pickImage(source: ImageSource.gallery);
    if (pickedFile != null) {
      final File imageFile = File(pickedFile.path);

      // Compress the image to reduce the size
      final compressedFile = await FlutterImageCompress.compressWithFile(
        imageFile.absolute.path,
        minWidth: 800,
        minHeight: 800,
        quality: 80,
      );

      if (compressedFile != null) {
        // Convert the compressed image to Base64 string
        final base64String = base64Encode(compressedFile);
        return base64String;
      }
    }
    return null;
  }
}
