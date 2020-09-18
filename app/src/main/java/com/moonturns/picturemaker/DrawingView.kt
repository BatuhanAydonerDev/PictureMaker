package com.moonturns.picturemaker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlin.reflect.typeOf

class DrawingView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    companion object {
        val SMALL_BRUSH_SIZE = 10f
        val NORMAL_BRUSH_SIZE = 20f
        val LARGE_BRUSH_SIZE = 30f
    }

    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mCanvas: Canvas? = null
    private var color = Color.BLACK
    private var mBrushSize = 0.toFloat()

    private var mPaths = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mDrawPaint!!.color = color
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.style = Paint.Style.STROKE

        mCanvasPaint = Paint(Paint.DITHER_FLAG)

        mDrawPath = CustomPath(color, mBrushSize)
        mBrushSize = NORMAL_BRUSH_SIZE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint!!)

        for (path in mPaths) {
            mDrawPaint!!.color = path.color
            mDrawPaint!!.strokeWidth = path.mBrushThickness
            canvas!!.drawPath(path, mDrawPaint!!)
        }

        mDrawPaint!!.color = mDrawPath!!.color
        mDrawPaint!!.strokeWidth = mDrawPath!!.mBrushThickness
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.style = Paint.Style.STROKE
        canvas!!.drawPath(mDrawPath!!, mDrawPaint!!)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        var x = event!!.x
        var y = event!!.y

        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color
                mDrawPath!!.mBrushThickness = mBrushSize

                mDrawPath!!.moveTo(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                mDrawPath!!.lineTo(x, y)
            }
            MotionEvent.ACTION_UP -> {
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> {
                return false
            }
        }
        invalidate()
        return true
    }

    fun setSizeForBrush(newSize: Float) {
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize
    }

    internal inner class CustomPath(var color: Int, var mBrushThickness: Float): Path() {}

}