package com.tera.progressbar_anim

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.BlurMaskFilter.Blur
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
import kotlin.math.min

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

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintArc = Paint(Paint.ANTI_ALIAS_FLAG)

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
    private var mBlurWidth = 0        // Толщина размытия
    private var mBlurStyle: Blur? = null // Стиль размытия


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

        mBlurWidth = a.getDimensionPixelSize(R.styleable.ProgressBarAnim_itemBlurWidth, 0)
        val blurStyle = a.getInt(R.styleable.ProgressBarAnim_itemBlurStyle, 0)
        a.recycle()

        mBlurStyle = if (blurStyle == 0) Blur.NORMAL
        else Blur.SOLID
        setBlur()

        mDAlpha = 255f / mItemsCount
        Color.colorToHSV(mItemsColor, mColorHsv) // Цвет Hsv

        mPaintArc.color = mItemsColor
        mPaintArc.style = Paint.Style.STROKE
    }

    private fun setBlur() {
        if (mBlurStyle != null && mBlurWidth > 0 && mItemStyle == 2) {
            setLayerType(LAYER_TYPE_SOFTWARE, mPaint)
            mPaint.setMaskFilter(BlurMaskFilter(mBlurWidth.toFloat(), mBlurStyle))
        } else {
            mPaint.setMaskFilter(null)
        }
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
        else if (mItemStyle == 3)
            drawCircle(canvas)
        else
            drawArcRandom(canvas)
        // Оси
        //drawAxes(canvas)
    }

    private fun drawDash(canvas: Canvas) {
        mPaint.style = Paint.Style.FILL

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

        val angle = -360f / mItemsCount
        canvas.save()
        for (i in 0..<mItemsCount) {
            var alpha = 255 - (mDAlpha * i * k).toInt()
            if (alpha < 0) alpha = 0
            val color = Color.HSVToColor(alpha, mColorHsv)
            mPaint.color = color
            canvas.drawRoundRect(x1, y1, x2, y2, r, r, mPaint)
            canvas.rotate(angle, mXc, mYc)
        }
        canvas.restore()
    }

    private fun drawCircle(canvas: Canvas) {
        mPaint.style = Paint.Style.FILL
        val r = if (mItemWidth == 0) mRadius * 0.19f
        else mItemWidth.toFloat()

        val y = mYc - mRadius + r
        val k = 1.2f

        val angle = -360f / mItemsCount
        canvas.save()
        for (i in 0..<mItemsCount) {
            var alpha = 255 - (mDAlpha * i * k).toInt()
            if (alpha < 0) alpha = 0
            val color = Color.HSVToColor(alpha, mColorHsv)
            mPaint.color = color
            canvas.drawCircle(mXc, y, r, mPaint)
            canvas.rotate(angle, mXc, mYc)
        }
        canvas.restore()
    }

    private fun drawArrows(canvas: Canvas) {
        mPaint.color = mItemsColor
        mPaint.style = Paint.Style.FILL
        setArcArrow()
        val a1 = 5f
        val a2 = -120f

        canvas.drawArc(mRect, a1, a2, false, mPaintArc)
        canvas.drawPath(mPath, mPaint)
        canvas.save()
        canvas.rotate(180f, mXc, mYc)
        canvas.drawArc(mRect, a1, a2, false, mPaintArc)
        canvas.drawPath(mPath, mPaint)
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
        mPaint.style = Paint.Style.STROKE
        var h: Float = if (mItemHeight == 0) mRadius * 0.4f
        else mItemHeight.toFloat()

        if (h > mRadius) h = mRadius

        val colorStart: Int = if (mItemsColorEnd == 0)
            Color.HSVToColor(0, mColorHsv)
        else mItemsColorEnd

        mShaderArc = SweepGradient(mXc, mYc, colorStart, mItemsColor)
        mPaint.strokeWidth = h
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.setShader(mShaderArc)

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
            a1 = -Math.toDegrees(asin(h / 2 / r.toDouble())).toFloat()
            a2 = -360f - a1 * 2
        }
        canvas.drawArc(rect, a1, a2, false, mPaint)
    }

    private fun drawArcRandom(canvas: Canvas) {
        var h: Float = if (mItemHeight == 0) mRadius * 0.3f
        else mItemHeight.toFloat()

        if (h > mRadius) h = mRadius

        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = h

        val r = mRadius - h / 2
        val x1 = mXc - r
        val y1 = mYc - r
        val x2 = mXc + r
        val y2 = mYc + r
        mRect = RectF(x1, y1, x2, y2)

        val a1 = 0f
        val a2 = -360f / mItemsCount

        for (i in 0..<mItemsCount) {
            val color = (Math.random() * 16777215).toInt() or (0xFF shl 24)
            mPaint.color = color
            canvas.drawArc(
                mRect, a1 + i * a2,
                a2, false, mPaint
            )
        }
    }

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
        val dA = -360f / mItemsCount

        val drawable = ContextCompat.getDrawable(context, mItemIcon) as Drawable
        drawable.setBounds(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())

        canvas.save()
        for (i in 0..<mItemsCount) {
            var alpha = 255 - (mDAlpha * i * k).toInt()
            if (alpha < 0) alpha = 0
            val color = Color.HSVToColor(alpha, mColorHsv)
            DrawableCompat.setTint(drawable, color)
            canvas.rotate(dA, mXc, mYc)
            drawable.draw(canvas)
        }
        canvas.restore()
    }

    private fun drawAxes(canvas: Canvas) {
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.RED
        mPaint.strokeWidth = mAxesWidth

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
        val wC = (mRadius * 2).toInt() + mBlurWidth
        setMeasuredDimension(
            resolveSize(wC, widthMeasureSpec),
            resolveSize(wC, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRadius = min(w, h) / 2f - mBlurWidth
        mXc = w / 2f
        mYc = h / 2f
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