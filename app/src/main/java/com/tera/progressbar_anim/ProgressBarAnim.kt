package com.tera.progressbar_anim

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.math.asin

class ProgressBarAnim(
    context: Context,
    attrs: AttributeSet?,
    defStyleRes: Int
) : View(context, attrs, defStyleRes) {


    constructor(context: Context, attributesSet: AttributeSet?) :
            this(context, attributesSet, 0)

    constructor(context: Context) : this(context, null)

    companion object {
        const val RADIUS = 100f
        const val ITEM_COUNT = 10
        const val ITEM_COLOR = Color.BLUE
        const val DURATION = 1500

    }

    private val mPaintDash = Paint()
    private val mPaintArc = Paint()
    private val mPaintArrow = Paint()
    private val mPaintGrad = Paint()
    private val mPaint = Paint()

    private var mRadius = RADIUS
    private var mRadiusArc = 0f
    private var mXc = 0f
    private var mYc = 0f
    private val mColorHsv = FloatArray(3)
    private var mDAlpha = 0f
    private var mRect = RectF()
    private val mPath = Path()
    private var mArcWidth = 0f
    private var mAnimator: ObjectAnimator? = null
    private var mHandler = Handler(Looper.getMainLooper())
    private var mShaderArc: Shader? = null
    private var mAxesWidth = 1f

    // Attributes
    private var mItemsColor = 0
    private var mItemsColorEnd = 0
    private var mItemsCount = 0
    private var mItemIcon = 0
    private var mItemHeight = 0
    private var mItemWidth = 0
    private var mItemStyle = 0
    private var mDuration = 0L

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ProgressBarAnim)
        mItemsColor = a.getColor(R.styleable.ProgressBarAnim_itemColor, ITEM_COLOR)
        mItemsColorEnd = a.getColor(R.styleable.ProgressBarAnim_itemColorEnd, 0)
        mItemsCount = a.getInt(R.styleable.ProgressBarAnim_itemCount, ITEM_COUNT)
        mItemIcon = a.getResourceId(R.styleable.ProgressBarAnim_itemIcon, 0)
        mDuration = a.getInt(R.styleable.ProgressBarAnim_animDuration, DURATION).toLong()

        mItemHeight = a.getDimensionPixelSize(R.styleable.ProgressBarAnim_itemHeight, 0)
        mItemWidth = a.getDimensionPixelSize(R.styleable.ProgressBarAnim_itemWidth, 0)
        mItemStyle = a.getInt(R.styleable.ProgressBarAnim_itemStyle, 0)

        a.recycle()

        initPaints()

        mDAlpha = 255f / mItemsCount
        Color.colorToHSV(mItemsColor, mColorHsv) // Цвет Hsv

    }

    private fun initPaints() {
        mPaintDash.color = mItemsColor
        mPaintDash.isAntiAlias = true

        mPaintArc.color = mItemsColor
        mPaintArc.isAntiAlias = true
        mPaintArc.style = Paint.Style.STROKE

        mPaintGrad.isAntiAlias = true
        mPaintGrad.style = Paint.Style.STROKE

        mPaintArrow.color = mItemsColor
        mPaintArrow.isAntiAlias = true

        mPaint.color = Color.RED
        mPaint.strokeWidth = mAxesWidth
        mPaint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mItemIcon != 0)
            drawItemsIcon(canvas)
        else if (mItemStyle == 0)
            drawDash(canvas)
        else if (mItemStyle == 1)
            drawArrows(canvas)
        else if (mItemStyle == 2)
            drawGrad(canvas)
        else
            drawCircle(canvas)

        //drawAxes(canvas)
    }

    private fun drawDash(canvas: Canvas) {

        val w = if (mItemWidth == 0) mRadius * 0.29f
        else mItemWidth.toFloat()

        val h = if (mItemHeight == 0) w * 1.6f
        else mItemHeight.toFloat()

        val r = w / 2
        val x1 = mXc - r
        val y1 = mYc - mRadius
        val x2 = mXc + r
        val y2 = y1 + h

        val k = 1.2f
        var angle: Float
        val dA = -360f / mItemsCount

        for (i in 0..<mItemsCount) {
            angle = i * dA
            var alpha = 255 - (mDAlpha * i * k).toInt()
            if (alpha < 0) alpha = 0
            val color = Color.HSVToColor(alpha, mColorHsv)
            mPaintDash.color = color
            canvas.save()
            canvas.rotate(angle, mXc, mYc)
            canvas.drawRoundRect(x1, y1, x2, y2, r, r, mPaintDash)
            canvas.restore()
        }
    }

    private fun drawCircle(canvas: Canvas) {
        val r = if (mItemWidth == 0) mRadius * 0.19f
        else mItemWidth.toFloat()

        val y = mYc - mRadius + r
        val k = 1.2f
        var angle: Float
        val dA = -360f / mItemsCount

        for (i in 0..<mItemsCount) {
            angle = i * dA
            var alpha = 255 - (mDAlpha * i * k).toInt()
            if (alpha < 0) alpha = 0
            val color = Color.HSVToColor(alpha, mColorHsv)
            mPaintDash.color = color
            canvas.save()
            canvas.rotate(angle, mXc, mYc)
            canvas.drawCircle(mXc, y, r, mPaintDash)
            canvas.restore()
        }
    }

    private fun drawArrows(canvas: Canvas) {
        setArcArrow()
        val a1 = 5f
        val a2 = -120f

        canvas.drawArc(mRect, a1, a2, false, mPaintArc)
        canvas.drawPath(mPath, mPaintArrow)

        canvas.save()
        canvas.rotate(180f, mXc, mYc)
        canvas.drawArc(mRect, a1, a2, false, mPaintArc)
        canvas.drawPath(mPath, mPaintArrow)
        canvas.restore()
    }

    private fun setArcArrow() {
        mRadiusArc = mRadius * 0.64f
        val r = mRadiusArc
        val x1 = mXc - r
        val y1 = mYc - r
        val x2 = mXc + r
        val y2 = mYc + r
        mRect = RectF(x1, y1, x2, y2)

        mArcWidth = mRadius * 0.2f
        mPaintArc.strokeWidth = mArcWidth

        val arrow = mArcWidth * 1.7f
        val xA1 = mXc + r
        val xA2 = xA1 + arrow
        val xA3 = xA1 - arrow

        mPath.moveTo(xA1, mYc)
        mPath.lineTo(xA2, mYc)
        mPath.lineTo(xA1, mYc + arrow * 1.1f)
        mPath.lineTo(xA3, mYc)
        mPath.close()
    }

    private fun drawGrad(canvas: Canvas) {

        var h: Float = if (mItemHeight == 0) mRadius * 0.4f
        else mItemHeight.toFloat()

        if (h > mRadius) h = mRadius

        val colorStart: Int = if (mItemsColorEnd == 0)
            Color.HSVToColor(0, mColorHsv)
        else mItemsColorEnd

        mShaderArc = SweepGradient(mXc, mYc, colorStart, mItemsColor)
        mPaintGrad.strokeWidth = h
        mPaintGrad.strokeCap = Paint.Cap.ROUND
        mPaintGrad.setShader(mShaderArc)

        val r = mRadius - h / 2
        val x1 = mXc - r
        val y1 = mYc - r
        val x2 = mXc + r
        val y2 = mYc + r
        val rect = RectF(x1, y1, x2, y2)

        val a1: Float
        val a2: Float

        if (h > mRadius * 0.6) {
            a1 = 0f
            a2 = 360f
        } else {
            a1 = -radiansToDegrees(asin(h / 2 / r.toDouble())).toFloat()
            a2 = -360f - a1 * 2
        }
        canvas.drawArc(rect, a1, a2, false, mPaintGrad)
    }

    private fun radiansToDegrees(radians: Double): Double {
        return radians * 180 / Math.PI
    }

    // Icon
    private fun drawItemsIcon(canvas: Canvas) {

        val w = if (mItemWidth == 0) mRadius * 0.25f
        else mItemWidth.toFloat()

        val h = if (mItemHeight == 0) w * 2
        else mItemHeight.toFloat()

        val x1 = mXc - w
        val y1 = mYc - mRadius
        val x2 = mXc + w
        val y2 = y1 + h
        val k = 1.2f
        var angle: Float
        val dA = -360f / mItemsCount

        val drawable = ContextCompat.getDrawable(context, mItemIcon) as Drawable
        drawable.setBounds(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())

        canvas.save()
        for (i in 0..<mItemsCount) {
            angle = i * dA
            var alpha = 255 - (mDAlpha * i * k).toInt()
            if (alpha < 0) alpha = 0
            val color = Color.HSVToColor(alpha, mColorHsv)
            DrawableCompat.setTint(drawable, color)
            canvas.save()
            canvas.rotate(angle, mXc, mYc)
            drawable.draw(canvas)
            canvas.restore()
        }
        canvas.restore()
    }

    private fun drawAxes(canvas: Canvas) {
        val r = mRadius - mAxesWidth / 2
        canvas.drawCircle(mXc, mYc, r, mPaint)
        canvas.drawLine(0f, mYc, width.toFloat(), mYc, mPaint)
    }

    // Animation
    private fun startAnim() {
        mAnimator = ObjectAnimator.ofFloat(this, "rotation", 360f)
        mAnimator?.duration = mDuration
        mAnimator?.repeatCount = ValueAnimator.INFINITE
        mAnimator?.repeatMode = ValueAnimator.RESTART
        mAnimator?.interpolator = LinearInterpolator()
        mAnimator?.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val wC = (mRadius * 2).toInt()
        setMeasuredDimension(
            resolveSize(wC, widthMeasureSpec),
            resolveSize(wC, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (w > h) {
            mRadius = h / 2f
            val dW = (w - h) / 2
            mXc = mRadius + dW
            mYc = mRadius
        } else {
            mRadius = w / 2f
            val dY = (h - w) / 2
            mXc = mRadius
            mYc = mRadius + dY
        }
        startAnim()
    }

    var animation: Boolean = false
        set(value) {
            field = value
            if (!value)
                mHandler.postDelayed({
                    mAnimator?.pause()
                }, 100)
            else {
                mHandler.postDelayed({
                mAnimator?.resume()
                }, 100)
            }
        }


}