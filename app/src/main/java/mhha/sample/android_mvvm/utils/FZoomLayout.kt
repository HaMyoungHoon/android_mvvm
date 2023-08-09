package mhha.sample.android_mvvm.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.absoluteValue
import kotlin.math.sign

class FZoomLayout: ConstraintLayout, ScaleGestureDetector.OnScaleGestureListener {
    companion object {
        enum class ZOOM(val data: Float) {
            MIN(1F),
            MAX(3F)
        }
        enum class MODE {
            NONE,
            DRAG,
            ZOOM
        }
    }
    private var _mode = MODE.NONE
    private var _scale = 1.0F
    private var _lastScaleFactor = 0F

    private var _startX = 0F
    private var _startY = 0F

    private var _dx = 0F
    private var _dy = 0F
    private var _prevDx = 0F
    private var _prevDy = 0F

    constructor(context : Context) : super(context) {
        init(context)
    }

    constructor(context : Context, attrs : AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context : Context, attrs : AttributeSet?, defStyle : Int) : super(context, attrs, defStyle) {
        init(context)
    }

    fun init(context : Context) {
        val scaleDetector = ScaleGestureDetector(context, this)
        setOnTouchListener { view, motionEvent ->
            when (motionEvent.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    if (_scale > ZOOM.MIN.data) {
                        _mode = MODE.DRAG
                        _startX = motionEvent.x - _prevDx
                        _startY = motionEvent.y - _prevDy
                    }
                }
                MotionEvent.ACTION_MOVE -> if (_mode == MODE.DRAG) {
                    _dx = motionEvent.x - _startX
                    _dy = motionEvent.y - _startY
                }
                MotionEvent.ACTION_POINTER_DOWN -> _mode = MODE.ZOOM
                MotionEvent.ACTION_POINTER_UP -> _mode = MODE.NONE
                MotionEvent.ACTION_UP -> {
                    _mode = MODE.NONE
                    _prevDx = _dx
                    _prevDy = _dy
                    val xDiff = (motionEvent.x - _startX).absoluteValue
                    val yDiff = (motionEvent.y - _startY).absoluteValue
                    if (xDiff < 3F && yDiff < 3F) {
                        performClick()
                    }
                }
            }
            scaleDetector.onTouchEvent(motionEvent)
            val child = child()
            if (_mode == MODE.DRAG && _scale >= ZOOM.MIN.data || _mode == MODE.ZOOM) {
                parent.requestDisallowInterceptTouchEvent(true)
                val maxDx: Float = (child.width - child.width / _scale) / 2 * _scale
                val maxDy: Float = (child.height - child.height / _scale) / 2 * _scale
                _dx = Math.min(Math.max(_dx, -maxDx), maxDx)
                _dy = Math.min(Math.max(_dy, -maxDy), maxDy)
                applyScaleAndTranslation()
            }
            true
        }
    }

    // ScaleGestureDetector
    override fun onScaleBegin(scaleDetector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScale(scaleDetector: ScaleGestureDetector): Boolean {
        val scaleFactor = scaleDetector.scaleFactor
        if (_lastScaleFactor == 0F || sign(scaleFactor) == sign(_lastScaleFactor)) {
            _scale *= scaleFactor
            _scale = ZOOM.MIN.data.coerceAtLeast(_scale.coerceAtMost(ZOOM.MAX.data))
            _lastScaleFactor = scaleFactor
            applyScaleAndTranslation()
        } else {
            _lastScaleFactor = 0F
        }
        return true
    }

    override fun onScaleEnd(scaleDetector: ScaleGestureDetector) {
    }

    fun resetScale() {
        _scale = 1F
        _dx = 0F
        _dy = 0F
        applyScaleAndTranslation()
    }
    private fun applyScaleAndTranslation() {
        val child = child()
        child.scaleX = _scale
        child.scaleY = _scale
        child.translationX = _dx
        child.translationY = _dy
        // 이거 별로임
//       for (i in 0..childCount)
//       {
//           val child = child(i) ?: return
//           child.scaleX = _scale
//           child.scaleY = _scale
//           child.translationX = _dx
//           child.translationY = _dy
//       }
    }

    private fun child(index : Int = 0) : View {
        return getChildAt(0)
        // 이거 별로임
//        if (index > childCount)
//        {
//            return null
//        }
//
//        return getChildAt(index)
    }
}