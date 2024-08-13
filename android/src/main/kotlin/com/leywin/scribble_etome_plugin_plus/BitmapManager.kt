package com.leywin.scribble_etome_plugin_plus

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.util.Base64
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BitmapManager {
    private const val SCREEN_WIDTH = 1200
    private const val SCREEN_HEIGHT = 1600

    fun createBlankBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(SCREEN_WIDTH, SCREEN_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        return bitmap
    }

    fun decodeBase64ToBitmap(base64String: String): Bitmap {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }


    fun loadBitmapFromPath(filePath: String): Bitmap {
        val file = File(filePath)
        if (!file.exists()) {
            Log.e("HandwrittenView", "File not found: $filePath")
            return createBlankBitmap()
        }

        val options = BitmapFactory.Options().apply {
            inPreferredConfig = Bitmap.Config.ARGB_8888
            inScaled = false
            inMutable = true
        }

        return try {
            BitmapFactory.decodeFile(filePath, options) ?: run {
                Log.e("HandwrittenView", "Failed to decode bitmap from file: $filePath")
                createBlankBitmap()
            }
        } catch (e: Exception) {
            Log.e("HandwrittenView", "Error loading bitmap: ${e.localizedMessage}", e)
            createBlankBitmap()
        }
    }

    fun saveBitmapFromPath(
        bitmap: Bitmap,
        imageName: String?,
        savePath: String
    ): Pair<Boolean, String?> {
        val directory = File(savePath)
        if (!directory.exists() && !directory.mkdirs()) {
            val errMsg = "Failed to create directory: $savePath"
            Log.e("HandwrittenView", errMsg)
            return Pair(false, errMsg)
        }

        val fileName =
            imageName ?: SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())
        val filePath = "$savePath${fileName}.png"
        val file = File(filePath)

        if (file.exists()) {
            val deleted = file.delete()
            if (!deleted) {
                val errMsg = "Failed to delete existing file: $filePath"
                Log.e("HandwrittenView", errMsg)
                return Pair(false, errMsg)
            }
        }

        return try {
            FileOutputStream(filePath).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos)
            }
            Pair(true, null)
        } catch (e: IOException) {
            val errMsg = "Error saving bitmap: ${e.localizedMessage}"
            Log.e("HandwrittenView", errMsg, e)
            Pair(false, errMsg)
        }
    }
}
