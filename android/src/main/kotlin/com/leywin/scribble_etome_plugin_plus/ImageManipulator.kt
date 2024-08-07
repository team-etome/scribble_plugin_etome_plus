package com.leywin.scribble_etome_plugin_plus

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.handwritten.HandwrittenView
import android.widget.ImageView
import android.widget.RelativeLayout
import kotlin.math.max
import kotlin.math.min

class ImageManipulator(private val parentLayout: RelativeLayout, private val handwrittenView: HandwrittenView) {
    private var imageView: ImageView? = null
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var dX = 0f
    private var dY = 0f
    private var scaleFactor = 1f
    private var scaleGestureDetector: ScaleGestureDetector? = null

    init {
        scaleGestureDetector = ScaleGestureDetector(parentLayout.context, ScaleListener())
    }

    fun addImage(base64String: String?) {
        if (base64String != null) {
            val decodedString = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

            imageView = ImageView(parentLayout.context)
            imageView?.apply {
                setImageBitmap(bitmap)
                setBackgroundColor(Color.TRANSPARENT)
                setOnTouchListener { view, event ->
                    handleTouch(view, event)
                    scaleGestureDetector?.onTouchEvent(event)
                    true
                }
                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                parentLayout.addView(this, params)
            }
        }
    }

    private fun handleTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                dX = view.x - initialTouchX
                dY = view.y - initialTouchY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!scaleGestureDetector!!.isInProgress) {
                    view.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                view.performClick()
                return true
            }
            else -> return false
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = max(0.1f, min(scaleFactor, 5.0f))
            imageView?.apply {
                scaleX = scaleFactor
                scaleY = scaleFactor
            }
            return true
        }
    }

    fun increaseSize() {
        imageView?.let {
            scaleFactor += 0.1f
            it.scaleX = scaleFactor
            it.scaleY = scaleFactor
        }
    }

    fun decreaseSize() {
        imageView?.let {
            scaleFactor = max(0.1f, scaleFactor - 0.1f)
            it.scaleX = scaleFactor
            it.scaleY = scaleFactor
        }
    }

    fun rotateRight() {
        imageView?.let {
            it.rotation += 15f
        }
    }

    fun rotateLeft() {
        imageView?.let {
            it.rotation -= 15f
        }
    }

    fun doneImage() {
        imageView?.let {
            val bitmap = handwrittenView.layer[handwrittenView.curLayerIndex]
            val canvas = Canvas(bitmap)
            val location = IntArray(2)
            it.getLocationOnScreen(location)
            val x = location[0].toFloat()
            val y = location[1].toFloat()

            val matrix = Matrix()
            matrix.postTranslate(x, y)
            matrix.postRotate(it.rotation, x + it.width / 2, y + it.height / 2)
            matrix.postScale(it.scaleX, it.scaleY, x + it.width / 2, y + it.height / 2)

            canvas.drawBitmap((it.drawable as BitmapDrawable).bitmap, matrix, Paint())

            parentLayout.removeView(it)
            imageView = null
        }
    }

    fun cancelImage() {
        imageView?.let {
            parentLayout.removeView(it)
            imageView = null
        }
    }
}
