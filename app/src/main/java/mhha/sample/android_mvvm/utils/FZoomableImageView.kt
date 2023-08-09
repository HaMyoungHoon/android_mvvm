package mhha.sample.android_mvvm.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Matrix.MTRANS_X
import android.graphics.Matrix.MTRANS_Y
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import mhha.sample.android_mvvm.interfaces.command.IEventListener
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class FZoomableImageView: AppCompatImageView, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    enum class Event(val index: Int) {
        NONE(0),
        DRAG(1),
        ZOOM(2),
        CLICK(3)
    }
    val _matrix = Matrix()
    var mode = Event.NONE
    val last = PointF()
    val start = PointF()
    val minScale = 1F
    var maxScale = 3F
    var saveScale = 1F
    val scaleArray = FloatArray(9)
    var _width = 0F
    var _height = 0F
    var origWidth = 0F
    var origHeight = 0F
    var oldWidth = 0
    var oldHeight = 0
    var scaleDetector: ScaleGestureDetector? = null
    var gestureDetector: GestureDetector? = null

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
        super.setClickable(true)
        setGestureDetector()
        setScaleDetector()
        imageMatrix = _matrix
        scaleType = ScaleType.MATRIX
        setThisTouchListener()
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        val origScale = saveScale
        var scaleFactor = 0F
        if (saveScale == maxScale) {
            saveScale = minScale
            scaleFactor = minScale / origScale
        } else {
            saveScale = maxScale
            scaleFactor = maxScale / origScale
        }
        _matrix.postScale(scaleFactor, scaleFactor, (_width / 2), (_height / 2))
        fixTrans()
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        return false
    }
    override fun onDown(e: MotionEvent): Boolean {
        return false
    }
    override fun onShowPress(e: MotionEvent) {
    }
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }
    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return false
    }
    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        return false
    }
    override fun onLongPress(e: MotionEvent) {
    }
    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        return false
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        _width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        _height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        if (oldWidth == _width.toInt() && oldHeight == _height.toInt() || _width == 0F || _height == 0F) {
            return
        }
        oldHeight = _height.toInt()
        oldWidth = _width.toInt()
        if (saveScale == 1F) {
            if (drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0) {
                return
            }
            val bmWidth = drawable.intrinsicWidth
            val bmHeight = drawable.intrinsicHeight
            val scaleX = _width / bmWidth
            val scaleY = _height / bmHeight
            val scale = scaleX.coerceAtMost(scaleY)
            _matrix.setScale(scale, scale)
            var redundantYSpace = _height - (scale * bmHeight)
            var redundantXSpace = _width - (scale * bmWidth)
            redundantYSpace /= 2
            redundantXSpace /= 2
            _matrix.postTranslate(redundantXSpace, redundantYSpace)
            origWidth = _width - 2 * redundantXSpace
            origHeight = _height - 2 * redundantYSpace
            origWidth = _width - 2 * redundantXSpace
            origHeight = _height - 2 * redundantYSpace
            imageMatrix = _matrix
        }
        fixTrans()
    }
    fun setMaxZoom(x: Float) {
        maxScale = x
    }
    private fun setScaleDetector() {
        scaleDetector = ScaleGestureDetector(context, object: ScaleGestureDetector.OnScaleGestureListener {
            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                mode = Event.ZOOM
                return true
            }
            override fun onScaleEnd(detector: ScaleGestureDetector) {
                mode = Event.NONE
            }
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                var scaleFactor = detector.scaleFactor
                val origScale = saveScale
                saveScale *= scaleFactor
                if (saveScale > maxScale) {
                    saveScale = maxScale
                    scaleFactor = maxScale / origScale
                } else if (saveScale < minScale) {
                    saveScale = minScale
                    scaleFactor = minScale / origScale
                }
                if (origWidth * saveScale <= _width || origHeight * saveScale <= _height) {
                    _matrix.postScale(scaleFactor, scaleFactor, _width /2, _height / 2)
                } else {
                    _matrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                }
                fixTrans()
                return true
            }
        })
    }
    private fun setGestureDetector() {
        gestureDetector = GestureDetector(context, this)
        gestureDetector?.setOnDoubleTapListener(this)
    }
    private fun setThisTouchListener() {
        setOnTouchListener(object: OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event == null) return true
                scaleDetector?.onTouchEvent(event)
                gestureDetector?.onTouchEvent(event)
                val current = PointF(event.x, event.y)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        last.set(current)
                        start.set(last)
                        mode = Event.DRAG
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (mode == Event.DRAG) {
                            val deltaX = current.x - last.x
                            val deltaY = current.y - last.y
                            val fixTransX = getFixDragTrans(deltaX, _width, origWidth * saveScale)
                            val fixTransY = getFixDragTrans(deltaY, _height, origHeight * saveScale)
                            _matrix.postTranslate(fixTransX, fixTransY)
                            fixTrans()
                            last.set(current.x, current.y)
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        mode = Event.NONE
                        val xDiff = (current.x - start.x).absoluteValue.toInt()
                        val yDiff = (current.y - start.y).absoluteValue.toInt()
                        if (xDiff < Event.CLICK.index && yDiff < Event.CLICK.index) {
                            performClick()
                        }
                    }
                    MotionEvent.ACTION_POINTER_UP -> mode = Event.NONE
                }
                imageMatrix = _matrix
                invalidate()
                return true
            }
        })
    }
    private fun fixTrans() {
        _matrix.getValues(scaleArray)
        val transX = scaleArray[MTRANS_X]
        val transY = scaleArray[MTRANS_Y]
        val fixTransX = getFixTrans(transX, _width, origWidth * saveScale)
        val fixTransY = getFixTrans(transY, _height, origHeight * saveScale)
        if (fixTransX != 0F || fixTransY != 0F) {
            _matrix.postTranslate(fixTransX, fixTransY)
        }
    }
    private fun getFixTrans(trans: Float, viewSize: Float, contentSize: Float): Float {
        var minTrans = 0F
        var maxTrans = 0F
        if (contentSize <= viewSize) {
            maxTrans = viewSize - contentSize
        } else {
            minTrans = viewSize - contentSize
        }
        if (trans < minTrans) {
            return -trans + minTrans
        }
        if (trans > maxTrans) {
            return -trans + maxTrans
        }
        return 0F
    }
    private fun getFixDragTrans(delta: Float, viewSize: Float, contentSize: Float): Float {
        if (contentSize <= viewSize) {
            return 0F
        }
        return delta
    }

    companion object {
        @JvmStatic
        @BindingAdapter("zoomable_image_view_url_source")
        fun setUrlSource(zoomableImageView: FZoomableImageView, zoomable_image_view_url_source: String?) {
            val urlSource = zoomable_image_view_url_source ?: return
            FGlideSupport.imageLoad(urlSource, zoomableImageView)
        }
        @JvmStatic
        @BindingAdapter("zoomable_image_view_uri_source")
        fun setUriSource(zoomableImageView: FZoomableImageView, zoomable_image_view_uri_source: Uri?) {
            val uriSource = zoomable_image_view_uri_source ?: return
            FGlideSupport.imageLoad(uriSource, zoomableImageView, resourceReadyListener = object: IEventListener {
                override fun onEvent(data: Any?) {
                    if (data !is ArrayList<*> || data.size < 5) return
                    val drawable = data[0] as? Drawable ?: return
                    zoomableImageView.invalidate()
                }
            })
        }
    }
}