package com.workfort.thinkndraw.util.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Paint.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.workfort.thinkndraw.app.data.local.fingerpath.FingerPath
import kotlin.math.abs

class PaintView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private var mX: Float = 0.toFloat()
    private var mY: Float = 0.toFloat()
    private var mPath: Path? = null
    private val mPaint: Paint = Paint()
    private val paths = ArrayList<FingerPath>()
    private var currentColor: Int = 0
    private var mBackgroundColor =
        DEFAULT_BG_COLOR
    private var strokeWidth: Int = 0
    private var emboss: Boolean = false
    private var blur: Boolean = false
    private val mEmboss: MaskFilter
    private val mBlur: MaskFilter
    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private val mBitmapPaint = Paint(DITHER_FLAG)

    init {
        mPaint.apply {
            color = BRUSH_COLOR
            style = Style.STROKE
            strokeJoin = Join.ROUND
            strokeCap = Cap.ROUND
            strokeWidth = 8f
            isAntiAlias = true
            isDither = true
            xfermode = null
            alpha = 0xff
        }

        @Suppress("DEPRECATION")
        mEmboss = EmbossMaskFilter(floatArrayOf(1f, 1f, 1f), 0.4f, 6f, 3.5f)
        mBlur = BlurMaskFilter(5f, BlurMaskFilter.Blur.NORMAL)

        init()
    }

    private fun init() {
        post {
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)?.apply {
                mBitmap = this
                mCanvas = Canvas(this)
            }
        }

        currentColor = BRUSH_COLOR
        strokeWidth = BRUSH_SIZE
    }

    fun brushSize(size: Int) {
        BRUSH_SIZE = size
    }

    fun normal() {
        emboss = false
        blur = false
    }

    fun emboss() {
        emboss = true
        blur = false
    }

    fun blur() {
        emboss = false
        blur = true
    }

    fun clear() {
        mBackgroundColor = DEFAULT_BG_COLOR
        paths.clear()
        normal()
        invalidate()
    }

    fun exportToBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        draw(canvas)
        return bitmap
    }

    fun exportToBitmap(width: Int, height: Int): Bitmap {
        val rawBitmap = exportToBitmap()
        val scaledBitmap = Bitmap.createScaledBitmap(rawBitmap, width, height, false)
        rawBitmap.recycle()
        return scaledBitmap
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()

        mCanvas?.drawColor(mBackgroundColor)

        for (fp in paths) {
            mPaint.color = fp.color
            mPaint.strokeWidth = fp.strokeWidth.toFloat()
            mPaint.maskFilter = null

            if (fp.emboss)
                mPaint.maskFilter = mEmboss
            else if (fp.blur)
                mPaint.maskFilter = mBlur

            mCanvas?.drawPath(fp.path, mPaint)

        }

        mBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, mBitmapPaint)
            canvas.restore()
        }
    }

    private fun touchStart(x: Float, y: Float) {
        mPath = Path()
        val fp =
            FingerPath(
                currentColor,
                emboss,
                blur,
                strokeWidth,
                mPath!!
            )
        paths.add(fp)

        mPath?.reset()
        mPath?.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = abs(x - mX)
        val dy = abs(y - mY)

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath?.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun touchUp() {
        mPath?.lineTo(mX, mY)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
        }

        return true
    }

    companion object {
        var BRUSH_SIZE = 15
        val BRUSH_COLOR = Color.BLACK
        const val DEFAULT_BG_COLOR = Color.WHITE
        private const val TOUCH_TOLERANCE = 4f
    }
}