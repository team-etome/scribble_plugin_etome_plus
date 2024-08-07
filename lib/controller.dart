import 'dart:developer';
import 'package:flutter/services.dart';
import 'package:scribble_etome_plugin_plus/models/save_result_model.dart';

/// Enum to represent different drawing tools.
enum DrawingTool {
  ballPointPen,
  fountainPen,
  pencil,
  highlight,
  linearEraser,
  areaEraser
}

class CanvasController {
  static const platform = MethodChannel('canvas_etome_options');

  /// Method to undo the last action.
  static Future<void> undo() async {
    try {
      await platform.invokeMethod('undo');
    } catch (e) {
      log("Error invoking undo method: $e");
    }
  }

  /// Method to redo the last undone action.
  static Future<void> redo() async {
    try {
      await platform.invokeMethod('redo');
    } catch (e) {
      log("Error invoking redo method: $e");
    }
  }

  /// Method to clear the canvas.
  static Future<void> clear() async {
    try {
      await platform.invokeMethod('clear');
    } catch (e) {
      log("Error invoking clear method: $e");
    }
  }

  /// Method to destroy the canvas.
  static Future<void> destroy() async {
    try {
      await platform.invokeMethod('destroy');
    } catch (e) {
      log("Error invoking destroy method: $e");
    }
  }

  /// Method to load a drawing from a image.
  static Future<void> load(String imageName) async {
    try {
      await platform.invokeMethod('load', {'imageName': imageName});
    } catch (e) {
      log("Error invoking load method: $e");
    }
  }

  /// Method to load a drawing from image using full path
  static Future<void> loadAbsolutePath(String fullPath) async {
    try {
      await platform.invokeMethod('loadAbsolutePath', {'fullPath': fullPath});
    } catch (e) {
      log("Error invoking loadAbsolutePath method: $e");
    }
  }

  /// Method to set the drawing tool (pen type).
  static Future<void> setDrawingTool(DrawingTool drawingType) async {
    try {
      await platform
          .invokeMethod('setDrawMode', {'strokeType': drawingType.index});
    } catch (e) {
      log("Error invoking setDrawingTool method: $e");
    }
  }

  /// Method to set the width of the pen.
  static Future<void> setStrokeWidth(int strokeWidth) async {
    try {
      await platform
          .invokeMethod('setStrokeWidth', {'strokeWidth': strokeWidth});
    } catch (e) {
      log("Error invoking setPenWidth method: $e");
    }
  }

  /// Method to set the width of the eraser.
  static Future<void> setEraserWidth(int eraserWidth) async {
    try {
      await platform
          .invokeMethod('setEraserWidth', {'eraserWidth': eraserWidth});
    } catch (e) {
      log("Error invoking setEraserWidth method: $e");
    }
  }

  /// Method to toggle handwriting mode.
  static Future<void> isHandwriting(bool isHandwriting) async {
    try {
      await platform
          .invokeMethod('isHandwriting', {'isHandwriting': isHandwriting});
    } catch (e) {
      log("Error invoking isHandwriting method: $e");
    }
  }

  /// Method to toggle overlay mode.
  static Future<void> onWindowFocusChanged(bool onWindowFocusChanged) async {
    try {
      await platform.invokeMethod('onWindowFocusChanged',
          {'onWindowFocusChanged': !onWindowFocusChanged});
    } catch (e) {
      log("Error invoking onWindowFocusChanged method: $e");
    }
  }

  /// Method to save the canvas drawing.
  static Future<SaveResult> save(String directoryPath,
      {bool doNotClear = false}) async {
    try {
      DateTime now = DateTime.now();
      final bitmap =
          await platform.invokeMethod('save', {"imageName": directoryPath});
      if (!doNotClear) {
        clear();
      }
      return SaveResult(
        bitmap: bitmap,
        dateTimeNow: now.toString(),
        directoryPath: directoryPath,
      );
    } catch (e) {
      log("Error invoking save method: $e");
      rethrow;
    }
  }

  /// Method to get the current drawing bitmap.
  static Future<int> getStrokeWidth() async {
    try {
      int penWidth = await platform.invokeMethod('getStrokeWidth');
      return penWidth;
    } catch (e) {
      log("Error invoking getStrokeWidth method: $e");
      rethrow;
    }
  }

  /// Method to get the current chosen penTool.
  static Future<DrawingTool> getDrawMode() async {
    try {
      int penStroke = await platform.invokeMethod('getDrawMode');
      switch (penStroke) {
        case 0:
          return DrawingTool.ballPointPen;
        case 1:
          return DrawingTool.fountainPen;
        case 2:
          return DrawingTool.pencil;
        case 3:
          return DrawingTool.highlight;
        case 4:
          return DrawingTool.linearEraser;
        case 5:
          return DrawingTool.areaEraser;
      }
      return DrawingTool.ballPointPen;
    } catch (e) {
      log("Error invoking getPenStroke method: $e");
      rethrow;
    }
  }

  /// Method to get the current drawing bitmap.
  static Future<bool> canUndo() async {
    try {
      bool canUndo = await platform.invokeMethod('canUndo');
      return canUndo;
    } catch (e) {
      log("Error invoking canUndo method: $e");
      rethrow;
    }
  }

  /// Method to get the current drawing bitmap.
  static Future<bool> canRedo() async {
    try {
      bool canRedo = await platform.invokeMethod('canRedo');
      return canRedo;
    } catch (e) {
      log("Error invoking canRedo method: $e");
      rethrow;
    }
  }

  /// Method to load the bitmap from bytearray.
  // static Future<void> loadBitmapFromByteArray(Uint8List byteArray) async {
  //   try {
  //     String base64String = base64Encode(byteArray);
  //     await platform.invokeMethod('loadBitmapFromByteArray', {
  //       'byteArray': base64String,
  //     });
  //   } catch (e) {
  //     log("Error invoking loadBitmapFromByteArray method: $e");
  //     rethrow;
  //   }
  // }

  // Checks if the canvas is dirty.
  static Future<bool> isDirty() async {
    try {
      bool isDirty = await platform.invokeMethod('isDirty');
      return isDirty;
    } catch (e) {
      log("Error invoking isDirty method: $e");
      rethrow;
    }
  }

  /// Method to get the current drawing bitmap.
  static Future<Uint8List> getBitmap() async {
    try {
      final List<dynamic> result = await platform.invokeMethod('getBitmap');
      return Uint8List.fromList(List<int>.from(result));
    } catch (e) {
      log("Error invoking getBitmap method: $e");
      rethrow;
    }
  }

  /// Checks if the canvas is currently being written on.
  // static Future<bool> isWriting() async {
  //   try {
  //     bool isWriting = await platform.invokeMethod('isWriting');
  //     return isWriting;
  //   } catch (e) {
  //     log("Error invoking isWriting method: $e");
  //     rethrow;
  //   }
  // }

  /// Checks if the canvas is currently being written on.
  // static Future<bool> isShown() async {
  //   try {
  //     bool isShown = await platform.invokeMethod('isShown');
  //     return isShown;
  //   } catch (e) {
  //     log("Error invoking isShown method: $e");
  //     rethrow;
  //   }
  // }

  /// Checks if the canvas is currently being written on.
  static Future<bool> isInEditMode() async {
    try {
      bool isInEditMode = await platform.invokeMethod('isInEditMode');
      return isInEditMode;
    } catch (e) {
      log("Error invoking isInEditMode method: $e");
      rethrow;
    }
  }

  /// Checks if the canvas has been edited.
  // static Future<bool> isEdited() async {
  //   try {
  //     bool isEdited = await platform.invokeMethod('isEdited');
  //     return isEdited;
  //   } catch (e) {
  //     log("Error invoking isWriting method: $e");
  //     rethrow;
  //   }
  // }

  /// Method to refresh current view.
  static Future<void> refreshCurrentView() async {
    try {
      await platform.invokeMethod('refreshCurrentView');
      log('refreshCurrentView');
    } catch (e) {
      log("Error invoking refreshCurrentView method: $e");
    }
  }

  /// Method to refresh drawable state.
  static Future<void> refreshDrawableState() async {
    try {
      await platform.invokeMethod('refreshDrawableState');
      log('refreshDrawableState');
    } catch (e) {
      log("Error invoking refreshDrawableState method: $e");
    }
  }

  /// Method to set elevation.
  static Future<void> setElevation(double elevation) async {
    try {
      await platform.invokeMethod('setElevation', {'elevation': elevation});
    } catch (e) {
      log("Error invoking setElevation method: $e");
    }
  }

  /// Method to set isHovered.
  static Future<void> isHovered(bool hovered) async {
    try {
      await platform.invokeMethod('isHovered', {'hovered': hovered});
    } catch (e) {
      log("Error invoking isHovered method: $e");
    }
  }

  /// Method to set addText.
  static Future<void> addText(String text) async {
    try {
      await platform.invokeMethod('text.addText', {'text': text});
    } catch (e) {
      log("Error invoking addText method: $e");
    }
  }

  /// Method to finish adding text and save it to the canvas.
  static Future<void> doneText() async {
    try {
      await platform.invokeMethod('text.doneText');
    } catch (e) {
      log("Error invoking doneText method: $e");
    }
  }

  /// Method to cancel text and remove it from canvas.
  static Future<void> cancelText() async {
    try {
      await platform.invokeMethod('text.cancelText');
    } catch (e) {
      log("Error invoking cancelText method: $e");
    }
  }

  /// Method to increase the text size.
  static Future<void> increaseTextSize() async {
    try {
      await platform.invokeMethod('text.increaseSize');
    } catch (e) {
      log("Error invoking increaseTextSize method: $e");
    }
  }

  /// Method to decrease the text size.
  static Future<void> decreaseTextSize() async {
    try {
      await platform.invokeMethod('text.decreaseSize');
    } catch (e) {
      log("Error invoking decreaseTextSize method: $e");
    }
  }

  /// Method to rotate the text to the right.
  static Future<void> rotateTextRight() async {
    try {
      await platform.invokeMethod('text.rotateRight');
    } catch (e) {
      log("Error invoking rotateTextRight method: $e");
    }
  }

  /// Method to rotate the text to the left.
  static Future<void> rotateTextLeft() async {
    try {
      await platform.invokeMethod('text.rotateLeft');
    } catch (e) {
      log("Error invoking rotateTextLeft method: $e");
    }
  }

  /// Method to add an image.
  static Future<void> addImage(String base64String) async {
    try {
      await platform.invokeMethod('image.addImage', {'base64String': base64String});
    } catch (e) {
      log("Error invoking addImage method: $e");
    }
  }

  /// Method to finish adding the image and save it to the canvas.
  static Future<void> doneImage() async {
    try {
      await platform.invokeMethod('image.doneImage');
    } catch (e) {
      log("Error invoking doneImage method: $e");
    }
  }

  /// Method to cancel and remove image from canvas.
  static Future<void> cancelImage() async {
    try {
      await platform.invokeMethod('image.cancelImage');
    } catch (e) {
      log("Error invoking cancelImage method: $e");
    }
  }

  /// Method to increase the image size.
  static Future<void> increaseImageSize() async {
    try {
      await platform.invokeMethod('image.increaseSize');
    } catch (e) {
      log("Error invoking increaseImageSize method: $e");
    }
  }

  /// Method to decrease the image size.
  static Future<void> decreaseImageSize() async {
    try {
      await platform.invokeMethod('image.decreaseSize');
    } catch (e) {
      log("Error invoking decreaseImageSize method: $e");
    }
  }

  /// Method to rotate the image to the right.
  static Future<void> rotateImageRight() async {
    try {
      await platform.invokeMethod('image.rotateRight');
    } catch (e) {
      log("Error invoking rotateImageRight method: $e");
    }
  }

  /// Method to rotate the image to the left.
  static Future<void> rotateImageLeft() async {
    try {
      await platform.invokeMethod('image.rotateLeft');
    } catch (e) {
      log("Error invoking rotateImageLeft method: $e");
    }
  }
}
