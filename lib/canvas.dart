import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:scribble_etome_plugin_plus/controller.dart';

///Main canvas, call it on a full sized page(or almost full sized page)
class CanvasEtome extends StatelessWidget {
  const CanvasEtome(
      {super.key,
      this.topSpaceHeight = 0,
      this.imageName,
      required this.saveFolder,
      this.drawingTool = DrawingTool.ballPointPen,
      this.penWidthValue = 3,
      this.isHandwriting = true,
      this.leftSideWidth,
      this.rightSideWidth,
      this.blackLineHeight = 1,
      this.bottomSpaceHeight});

  final int topSpaceHeight;
  final int? leftSideWidth;
  final int? rightSideWidth;
  final int? bottomSpaceHeight;
  final String? imageName;
  final String saveFolder;
  final int? blackLineHeight;
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
      "isHandwriting": isHandwriting,
      "leftSideWidth": leftSideWidth,
      "rightSideWidth": rightSideWidth,
      "bottomSpaceHeight": bottomSpaceHeight,
    };

    return AndroidView(
      viewType: viewType,
      layoutDirection: TextDirection.ltr,
      creationParams: creationParams,
      creationParamsCodec: const StandardMessageCodec(),
    );
  }
}
