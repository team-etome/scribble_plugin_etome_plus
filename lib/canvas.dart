import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:scribble_etome_plugin_plus/controller.dart';

///Main canvas, call it on a full sized page(or almost full sized page)
class CanvasEtome extends StatelessWidget {
  const CanvasEtome(
      {super.key,
      this.topSpaceHeight,
      this.imageName,
      required this.saveFolder,
      this.drawingTool = DrawingTool.ballPointPen,
      this.penWidthValue = 3,
      this.bottomSpaceHeight,
      this.leftSideWidth,
      this.rightSideWidth,
      this.isHandwriting = true});

  final int? topSpaceHeight;
  final int? bottomSpaceHeight;
  final int? leftSideWidth;
  final int? rightSideWidth;
  final String? imageName;
  final String saveFolder;
  final DrawingTool drawingTool;
  final int penWidthValue;
  final bool isHandwriting;

  @override
  Widget build(BuildContext context) {
    const String viewType = 'leywin/etome/scribble_etome';
    String saveFolderPath = '/storage/emulated/0/$saveFolder/';

    final Map<String, dynamic> creationParams = <String, dynamic>{
      "topSpaceHeight": topSpaceHeight,
      "imageName": imageName,
      "saveFolderPath": saveFolderPath,
      "drawingToolIndex": drawingTool.index,
      "penWidthValue": penWidthValue,
      "bottomSpaceHeight": bottomSpaceHeight,
      "leftSideWidth": leftSideWidth,
      "rightSideWidth": rightSideWidth,
      "isHandwriting": isHandwriting,
    };

    return AndroidView(
      viewType: viewType,
      layoutDirection: TextDirection.ltr,
      creationParams: creationParams,
      creationParamsCodec: const StandardMessageCodec(),
    );
  }
}
