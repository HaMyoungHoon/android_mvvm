package mhha.sample.android_mvvm.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import kotlin.math.min

class FCircleStrokeView: androidx.appcompat.widget.AppCompatImageView {
    lateinit var imagePath: Path
    lateinit var imageBorderPath: Path
    lateinit var borderPaint: Paint

    constructor(context: Context): super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init()
    }
    private fun init() {
        imagePath = Path()
        imageBorderPath = Path()
        borderPaint = Paint()
        borderPaint.color = Color.WHITE
        borderPaint.strokeCap = Paint.Cap.ROUND
        borderPaint.strokeWidth = 1F
        borderPaint.style = Paint.Style.STROKE
    }
    private fun calculatePath(radius: Float) {
        val centerX = measuredWidth / 2F
        val centerY = measuredHeight / 2F

        imagePath.reset()
        imagePath.addCircle(centerX, centerY, radius, Path.Direction.CW)
        imagePath.close()

        val radiusBorder = if (radius - 5F < 0F) 0F else radius - 5F
        imageBorderPath.reset()
        imageBorderPath.addCircle(centerX, centerY, radius, Path.Direction.CW)
        imageBorderPath.close()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null) {
            canvas.drawPath(imageBorderPath, borderPaint)
            canvas.clipPath(imagePath)
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        }
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
        calculatePath(min(width / 2F, height / 2F) - borderPaint.strokeWidth)
    }

    companion object {
        @JvmStatic
        @BindingAdapter("circle_stroke_view_radius")
        fun setRadius(circleStrokeView: FCircleStrokeView, circle_stroke_view_radius: Float) {
            circleStrokeView.calculatePath(circle_stroke_view_radius)
        }
        @JvmStatic
        @BindingAdapter("circle_stroke_view_border_color")
        fun setBorderColor(circleStrokeView: FCircleStrokeView, circle_stroke_view_border_color: Int) {
            circleStrokeView.borderPaint.color = circle_stroke_view_border_color
            circleStrokeView.invalidate()
        }
        @JvmStatic
        @BindingAdapter("circle_stroke_view_border_width")
        fun setBorderWidth(circleStrokeView: FCircleStrokeView, circle_stroke_view_border_width: Float) {
            if (circle_stroke_view_border_width >= 0F) {
                circleStrokeView.borderPaint.strokeWidth = circle_stroke_view_border_width
                circleStrokeView.invalidate()
            }
        }
        @JvmStatic
        @BindingAdapter("circle_stroke_view_source")
        fun setSource(circleStrokeView: FCircleStrokeView, circle_stroke_view_source: String) {
            FPicassoSupport.imageLoad(circle_stroke_view_source, circleStrokeView)
        }
    }
}