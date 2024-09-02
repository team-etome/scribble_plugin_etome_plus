package com.leywin.scribble_etome_plugin_plus

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.handwritten.HandwrittenView
import android.widget.ImageView
import android.widget.RelativeLayout
import com.leywin.scribble_etome_plugin_plus.BitmapManager.saveBitmapFromPath
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import java.io.ByteArrayOutputStream

class HandwrittenViewPlatformView(
    context: Context,
    creationParams: Map<*, *>?,
    channel: MethodChannel
) : PlatformView {
    private val handwrittenView: HandwrittenView
    private val layout: View
    private var savePath: String = "/storage/emulated/0/Notes"
    private var initFlag: Boolean = false

    private val imageManipulator: ImageManipulator

    init {
        val parentViewGroup = (context as? Activity)?.findViewById<ViewGroup>(android.R.id.content)

        layout =
            LayoutInflater.from(context).inflate(R.layout.activity_main, parentViewGroup, false)
        handwrittenView = layout.findViewById(R.id.handwrittenView)

        val handwrittenParent = layout.findViewById<RelativeLayout>(R.id.handwrittenParent)

        imageManipulator = ImageManipulator(handwrittenParent)

        savePath = creationParams?.get("saveFolderPath") as? String ?: savePath

        val density = context.resources.displayMetrics.density

        // AppBar space
        val topSpaceHeight = (creationParams?.get("topSpaceHeight") as? Double)?.toInt() ?: 45
        setViewSize(R.id.appBarSpace, topSpaceHeight, null, density)

        // Left space
        val leftSideWidth = (creationParams?.get("leftSideWidth") as? Double)?.toInt() ?: 0
        setViewSize(R.id.leftSideView, null, leftSideWidth, density)

        // Right space
        val rightSideWidth = (creationParams?.get("rightSideWidth") as? Double)?.toInt() ?: 0
        setViewSize(R.id.rightSideView, null, rightSideWidth, density)

        // Bottom space
        val bottomSpaceHeight = (creationParams?.get("bottomSpaceHeight") as? Double)?.toInt() ?: 0
        setViewSize(R.id.bottomSpace, bottomSpaceHeight, null, density)

        // Adjust HandwrittenView height
        adjustHandwrittenViewHeight(topSpaceHeight, density)

        setupHandwrittenView(creationParams!!)
        setupMethodChannel(channel)
    }

    private fun setupHandwrittenView(creationParams: Map<*, *>) {
        val drawingToolIndex =
            creationParams["drawingToolIndex"] as? Int ?: HandwrittenView.DRAW_MODE_BALLPEN
        val isHandwriting =
            creationParams["isHandwriting"] as? Boolean ?: true
        handwrittenView.drawMode = drawingToolIndex
        handwrittenView.enableHandwritten(isHandwriting)

        val layers = mutableListOf<Bitmap?>()
        layers.add(BitmapManager.createBlankBitmap())

        handwrittenView.layer = layers
        handwrittenView.curLayerIndex = 0

        handwrittenView.startThread()
        initFlag = true
    }

    private fun setViewSize(viewId: Int, heightDp: Int?, widthDp: Int?, density: Float) {
        val view = layout.findViewById<View>(viewId)
        val layoutParams = view.layoutParams as RelativeLayout.LayoutParams
        heightDp?.let {
            layoutParams.height = (it * density).toInt()
        }
        widthDp?.let {
            layoutParams.width = (it * density).toInt()
        }
        view.layoutParams = layoutParams
    }

    private fun adjustHandwrittenViewHeight(topSpaceHeightDp: Int, density: Float) {
        // Calculate the new height
        val newHeightPx =
            layout.resources.displayMetrics.heightPixels - (topSpaceHeightDp * density).toInt()
        val layoutParams = handwrittenView.layoutParams as RelativeLayout.LayoutParams
        layoutParams.height = newHeightPx
        handwrittenView.layoutParams = layoutParams
    }

    private fun setupMethodChannel(channel: MethodChannel) {
        channel.setMethodCallHandler { call, result ->
            when (call.method) {
                "undo" -> undo(result)
                "redo" -> redo(result)
                "clear" -> clear()
                "load" -> {
                    val imageName = call.argument<String>("imageName")
                    load(result, imageName!!)
                }

                "save" -> {
                    val imageName = call.argument<String>("imageName")
                    save(result, imageName!!)
                }

                "setStrokeWidth" -> {
                    val penWidth = call.argument<Int>("strokeWidth")
                    setStrokeWidth(penWidth ?: 0)
                }

                "clipImage" -> {
                    val top = call.argument<Double>("top") ?: 0.0
                    val left = call.argument<Double>("left") ?: 0.0
                    val density = call.argument<Double>("density") ?: 0.0
                    val base64String = call.argument<String>("base64String")
                    clipImage(top, left, base64String, density)
                }

                // New image manipulation methods
                "image.addImage" -> {
                    val base64string = call.argument<String>("base64String")
                    val top = call.argument<Boolean>("top")
                    imageManipulator.addImage(base64string, top)
                }

                "image.moveImage" -> {
                    val offsetX = call.argument<Double>("offsetX") ?: 0f
                    val offsetY = call.argument<Double>("offsetY") ?: 0f
                    imageManipulator.moveImage(offsetX.toFloat(), offsetY.toFloat())
                }

                "image.increaseSize" -> imageManipulator.increaseSize()
                "image.decreaseSize" -> imageManipulator.decreaseSize()
                "image.rotateRight" -> imageManipulator.rotateRight()
                "image.rotateLeft" -> imageManipulator.rotateLeft()
                "image.cancelImage" -> imageManipulator.cancelImage()

                "getStrokeWidth" -> getStrokeWidth(result)
                "getDrawMode" -> getDrawMode(result)
                "isDirty" -> isDirty(result)
                "getBitmap" -> getBitmap(result)
                "getWholeBitmap" -> getWholeBitmap(result)
                "isInEditMode" -> isInEditMode(result)
                "destroy" -> onDestroy()
                "refreshCurrentView" -> refreshCurrentView()
                "refreshDrawableState" -> refreshDrawableState()
                "setDrawMode" -> {
                    val strokeType = call.argument<Int>("strokeType")
                    setDrawMode(strokeType ?: 0)
                }

                "onWindowFocusChanged" -> {
                    val isOverlay = call.argument<Boolean>("onWindowFocusChanged")
                    onWindowFocusChanged(isOverlay ?: true)
                }

                "isHandwriting" -> {
                    val isHandwriting = call.argument<Boolean>("isHandwriting")
                    isHandwriting(isHandwriting ?: true)
                }

                "setHightlightOpacity" -> {
                    val opacity = call.argument<Int>("opacity")
                    setHightlightOpacity(opacity!!)
                }

                else -> result.notImplemented()
            }
        }
    }

    private fun setHightlightOpacity(opacity: Int) {
        handwrittenView.grayscale = opacity
    }

    private fun clipImage(top: Double, left: Double, base64String: String?, density: Double) {
        if (base64String != null) {
            val bitmap = BitmapManager.decodeBase64ToBitmap(base64String)

            val imageView = ImageView(layout.context)
            imageView.setImageBitmap(bitmap)

            val displayMetrics = layout.context.resources.displayMetrics
            val densityRatio = displayMetrics.density

            val adjustedLeft = (left * densityRatio / density).toInt()
            val adjustedTop = (top * densityRatio / density).toInt()

            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.leftMargin = adjustedLeft
            params.topMargin = adjustedTop

            val parentLayout = layout.findViewById<RelativeLayout>(R.id.handwrittenParent)
            parentLayout.addView(imageView, params)
        }
    }


    private fun clear() {
        Log.d("CanvasFunction", "clear button")
        handwrittenView.clearLayer(handwrittenView.curLayerIndex)
    }

    private fun undo(result: MethodChannel.Result) {
        handwrittenView.undo()
        result.success("Undo action performed")
    }

    private fun redo(result: MethodChannel.Result) {
        handwrittenView.redo()
        result.success("Redo action performed")
    }

    private fun getBitmap(result: MethodChannel.Result) {
        try {
            val bitmap = Bitmap.createBitmap(
                handwrittenView.width,
                handwrittenView.height,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)
            handwrittenView.draw(canvas)

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            result.success(byteArray.toList())
        } catch (e: Exception) {
            result.error("BITMAP_ERROR", "Failed to get bitmap", e.localizedMessage)
        }
    }

    private fun getWholeBitmap(result: MethodChannel.Result) {
        val handwrittenParent = layout.findViewById<RelativeLayout>(R.id.handwrittenParent)
        try {
            val bitmap = Bitmap.createBitmap(
                handwrittenParent.width,
                handwrittenParent.height,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)
            handwrittenParent.draw(canvas)

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            result.success(byteArray.toList())
        } catch (e: Exception) {
            result.error("BITMAP_ERROR", "Failed to get bitmap", e.localizedMessage)
        }
    }

    private fun refreshDrawableState() {
        handwrittenView.refreshDrawableState()
        Log.d("CanvasFunction", "refreshDrawableState")
    }

    private fun isHandwriting(isHandwriting: Boolean) {
        handwrittenView.enableHandwritten(isHandwriting)
    }

    private fun isDirty(result: MethodChannel.Result) {
        if (initFlag) {
            val isDirty: Boolean = handwrittenView.isDirty
            result.success(isDirty)
        }
    }

    private fun isInEditMode(result: MethodChannel.Result) {
        if (initFlag) {
            val isInEditMode: Boolean = handwrittenView.isInEditMode
            result.success(isInEditMode)
        }
    }

    private fun setDrawMode(drawMode: Int) {
        if (initFlag) {
            handwrittenView.drawMode = drawMode
        }
    }

    private fun onWindowFocusChanged(onWindowFocusChanged: Boolean) {
        if (initFlag) {
            handwrittenView.onWindowFocusChanged(onWindowFocusChanged)
        }
    }

    private fun refreshCurrentView() {
        handwrittenView.refreshCurrentView()
        Log.d("CanvasFunction", "refreshCurrentView")
    }

    private fun setStrokeWidth(strokeWidth: Int) {
        if (initFlag) {
            handwrittenView.strokeWidth = strokeWidth
        }
    }

    private fun getDrawMode(result: MethodChannel.Result) {
        val penStroke: Int = handwrittenView.drawMode
        result.success(penStroke)
    }

    private fun getStrokeWidth(result: MethodChannel.Result) {
        val penWidth: Int = handwrittenView.strokeWidth
        result.success(penWidth)
    }

    private fun save(result: MethodChannel.Result, imageName: String) {
        val bitmapList: List<Bitmap> = handwrittenView.layer
        val currentIndex: Int = handwrittenView.curLayerIndex
        val currentBitmap: Bitmap = bitmapList[currentIndex]
        val byteArrayOutputStream = ByteArrayOutputStream()
        currentBitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val (isSaved, errorMessage) = saveBitmapFromPath(currentBitmap, imageName, savePath)
        if (isSaved) {
            result.success(byteArray)
        } else {
            result.error("SAVE_ERROR", errorMessage, null)
        }
    }

    private fun load(result: MethodChannel.Result, imageName: String) {
        val filePath = "$savePath${imageName}.png"
        clear()
        val bitMap: Bitmap = BitmapManager.loadBitmapFromPath(filePath)
        val layers = mutableListOf<Bitmap?>()
        layers.add(bitMap)
        handwrittenView.layer = layers
        result.success("successfully loaded $filePath")
    }

    override fun getView(): View {
        return layout
    }

    override fun dispose() {
        handwrittenView.stopThread()
    }

    private fun onDestroy() {
        if (initFlag) {
            handwrittenView.clearLayer(handwrittenView.curLayerIndex)
        }
        handwrittenView.stopThread()
    }
}
