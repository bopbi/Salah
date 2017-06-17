package com.arjunalabs.serpong.android.salah.qibla

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.ViewGroup



/**
 * Created by bobbyadiprabowo on 5/28/17.
 */

class QiblaCompassView : View {

    private val paint = Paint()
    private val path = Path()
    private var center = 0F
    private val arrowColor = Color.RED
    private val circleColor = Color.BLUE
    private val bgColor = Color.TRANSPARENT

    constructor(context: Context?) : super(context) {
        initialize()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    fun initialize() {
        paint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = Math.min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }


    override fun onDraw(canvas: Canvas?) {

        if (width < height) {
            center = width / 2F
        } else {
            center = height / 2F
        }

        canvas?.drawColor(bgColor)
        drawCircle(canvas)
        drawArrow(canvas)

    }

    fun drawCircle(canvas: Canvas?) {
        paint.color = circleColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8F
        canvas?.drawCircle(center,center,center - (paint.strokeWidth / 2),paint)
    }

    fun drawArrow(canvas: Canvas?) {
        paint.color = arrowColor
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 0F

        path.moveTo(center, 0F)
        path.lineTo(center + 32F, center)
        path.lineTo(center - 32F, center)
        path.lineTo(center, 0F)

        canvas?.drawPath(path, paint)
    }

    fun rotateCompass(from : Float, to: Float) {
        val anim = RotateAnimation(from, to,
                Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F)
        anim.duration = 500
        anim.fillAfter = true
        anim.setInterpolator(context, android.R.anim.decelerate_interpolator)
        startAnimation(anim)
    }
}