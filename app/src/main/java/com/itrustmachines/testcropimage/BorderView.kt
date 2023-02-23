package com.itrustmachines.testcropimage

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View

class BorderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val BORDER_WIDTH = 5f
    }

    private val borderPaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = BORDER_WIDTH
        style = Paint.Style.STROKE
    }

    private val borderRect = RectF(0f, 0f, 0f, 0f)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        canvas?.drawRect(borderRect, borderPaint)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            canvas?.clipOutRect(borderRect)
        } else {
            canvas?.clipRect(borderRect, Region.Op.DIFFERENCE)
        }
        canvas?.drawColor(Color.parseColor("#40000000"))
        canvas?.restore()
    }

    fun setRect(rect: RectF) {
        borderRect.set(rect)
        postInvalidate()
    }

    fun getRect(): RectF {
        return RectF(
            borderRect.left + BORDER_WIDTH / 2f,
            borderRect.top + BORDER_WIDTH / 2f,
            borderRect.right - BORDER_WIDTH / 2f,
            borderRect.bottom - BORDER_WIDTH / 2f
        )
    }
}