import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:scribble_etome_plugin_plus/canvas.dart';
import 'package:scribble_etome_plugin_plus/controller.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  SystemChrome.setEnabledSystemUIMode(SystemUiMode.manual, overlays: []);
  runApp(const MyApp());
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
  bool isOverlay = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          const CanvasEtome(
            saveFolder: 'TestNotes',
          ),
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
                // IconButton(
                //   onPressed: () {
                //     CanvasController.setDrawingTool(DrawingTool.pencil);
                //   },
                //   icon: const Icon(Icons.window_outlined),
                // ),
                IconButton(
                  onPressed: () {
                    CanvasController.setDrawingTool(DrawingTool.fountainPen);
                  },
                  icon: const Icon(Icons.window_outlined),
                ),
                IconButton(
                  onPressed: () {
                    CanvasController.setDrawingTool(DrawingTool.areaEraser);
                  },
                  icon: const Icon(Icons.remove),
                ),
                IconButton(
                  onPressed: () {
                    CanvasController.setDrawingTool(DrawingTool.highlight);
                  },
                  icon: const Icon(Icons.remove),
                ),
                IconButton(
                  onPressed: () {
                    setState(() {
                      isOverlay = !isOverlay;
                    });
                    CanvasController.onWindowFocusChanged(isOverlay);
                  },
                  icon: const Icon(Icons.abc_outlined),
                ),
                IconButton(
                  onPressed: () {
                    _showAlertDialog(context);
                  },
                  icon: const Icon(
                    Icons.warning_amber_outlined,
                    color: Colors.red,
                  ),
                ),
              ],
            ),
          ),
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
}
