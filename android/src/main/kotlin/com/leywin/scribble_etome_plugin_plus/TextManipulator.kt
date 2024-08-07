package com.leywin.scribble_etome_plugin_plus

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.handwritten.HandwrittenView
import android.widget.RelativeLayout
import android.widget.TextView

class TextManipulator(private val parentLayout: RelativeLayout, private val handwrittenView: HandwrittenView) {
    private var textView: TextView? = null
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var dX = 0f
    private var dY = 0f

    fun addText(text: String?) {
        if (text != null) {
            textView = TextView(parentLayout.context)
            textView?.apply {
                this.text = text
                setTextColor(Color.BLACK)
                textSize = 20f
                setBackgroundColor(Color.TRANSPARENT)
                setOnTouchListener { view, event ->
                    handleTouch(view, event)
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

    private fun handleTouch(view: View, event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                dX = view.x - initialTouchX
                dY = view.y - initialTouchY
            }
            MotionEvent.ACTION_MOVE -> {
                view.animate()
                    .x(event.rawX + dX)
                    .y(event.rawY + dY)
                    .setDuration(0)
                    .start()
            }
        }
    }

    private fun pxToSp(px: Float, context: Context): Float {
        return px / context.resources.displayMetrics.density
    }

    private fun spToPx(sp: Float, context: Context): Float {
        return sp * context.resources.displayMetrics.density
    }

    fun increaseSize() {
        textView?.let {
            val currentSizeSp = pxToSp(it.textSize, it.context)
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentSizeSp + 1f)
        }
        Log.d("Text", "INCREASE")
    }

    fun decreaseSize() {
        textView?.let {
            val currentSizeSp = pxToSp(it.textSize, it.context)
            if (currentSizeSp > 1f) {
                it.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentSizeSp - 1f)
                Log.d("Text", "DECREASE")
            }
        }
    }

    fun rotateRight() {
        textView?.let {
            it.rotation += 15f
        }
    }

    fun rotateLeft() {
        textView?.let {
            it.rotation -= 15f
        }
    }

    fun doneText() {
        textView?.let {
            val bitmap = handwrittenView.layer[handwrittenView.curLayerIndex]
            val canvas = Canvas(bitmap)

            val location = IntArray(2)
            it.getLocationOnScreen(location)
            val parentLocation = IntArray(2)
            parentLayout.getLocationOnScreen(parentLocation)

            // Calculate the correct coordinates within the canvas
            val x = location[0].toFloat() - parentLocation[0]
            val y = location[1].toFloat() - parentLocation[1]

            val paint = Paint()
            paint.color = it.currentTextColor
            paint.textSize = it.textSize
            paint.typeface = it.typeface

            canvas.save()
            canvas.rotate(it.rotation, x + it.width / 2, y + it.height / 2)
            canvas.drawText(it.text.toString(), x, y + it.height / 2, paint)
            canvas.restore()

            parentLayout.removeView(it)
            textView = null
        }
    }


    fun cancelText() {
        textView?.let {
            parentLayout.removeView(it)
            textView = null
        }
    }
}
