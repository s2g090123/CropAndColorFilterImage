package com.itrustmachines.testcropimage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlin.math.max
import kotlin.math.roundToInt

class CropView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val root by lazy { findViewById<ConstraintLayout>(R.id.root) }
    private val image by lazy { findViewById<ImageView>(R.id.iv) }
    private val border by lazy { findViewById<BorderView>(R.id.border) }

    private var cropWidth = 0
    private var cropHeight = 0
    private var outputWidth: Int = 0
    private var outputHeight: Int = 0
    private var aspectRatio = 1f

    private val gestureListener by lazy {
        object : GestureListener() {
            private var lastMatrix = Matrix()
            private val originTargetRect by lazy {
                RectF(
                    image.left.toFloat(),
                    image.top.toFloat(),
                    image.right.toFloat(),
                    image.bottom.toFloat()
                )
            }
            private val originTargetCenter by lazy {
                PointF(originTargetRect.centerX(), originTargetRect.centerY())
            }

            fun resetMatrix() {
                lastMatrix = Matrix()
            }

            private fun RectF.getCenter(): PointF {
                return PointF(centerX(), centerY())
            }

            override fun onStart(event: MotionEvent): Boolean {
                return true
            }

            override fun onFinish(event: MotionEvent) {

            }

            override fun onGesture(
                event: MotionEvent,
                ds: Float,
                dr: Float,
                dx: Float,
                dy: Float,
                centerPoint: PointF
            ) {
                val newMatrix = Matrix(lastMatrix).apply {
                    postTranslate(dx, dy)
                    postScale(ds, ds, centerPoint.x, centerPoint.y)
                }
                val newRect = RectF(originTargetRect)
                newMatrix.mapRect(newRect, originTargetRect)
                lastMatrix = newMatrix
                val offset = newRect.getCenter().apply {
                    offset(-originTargetCenter.x, -originTargetCenter.y)
                }
                image.scaleX = (image.scaleX * ds).coerceAtLeast(0.5f)
                image.scaleY = (image.scaleY * ds).coerceAtLeast(0.5f)
                image.translationX = offset.x
                image.translationY = offset.y
            }

            override fun onDrag(event: MotionEvent, dx: Float, dy: Float) {
                val newMatrix = Matrix(lastMatrix).apply {
                    postTranslate(dx, dy)
                }
                val newRect = RectF(originTargetRect)
                newMatrix.mapRect(newRect, originTargetRect)
                lastMatrix = newMatrix
                val offset = newRect.getCenter().apply {
                    offset(-originTargetCenter.x, -originTargetCenter.y)
                }
                image.translationX = offset.x
                image.translationY = offset.y
            }
        }
    }

    init {
        View.inflate(context, R.layout.view_crop, this)
        setupRoot()
    }

    fun setImageUri(uri: Uri, cropWidth: Int, cropHeight: Int) {
        outputWidth = cropWidth
        outputHeight = cropHeight
        aspectRatio = cropWidth / cropHeight.toFloat()
        setBestCropSize()
        setupBorder()
        Glide.with(context)
            .asBitmap()
            .load(uri)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val borderRect = border.getRect()
                    val scale = max(
                        borderRect.width() / resource.width,
                        borderRect.height() / resource.height
                    )
                    val dstWidth = (resource.width * scale).roundToInt()
                    val dstHeight = (resource.height * scale).roundToInt()
                    val layoutParams = image.layoutParams as LayoutParams
                    image.layoutParams = layoutParams.apply {
                        width = dstWidth
                        height = dstHeight
                    }
                    image.scaleX = resource.width * scale / dstWidth
                    image.scaleY = resource.height * scale / dstHeight
                    image.setImageBitmap(resource)
                    image.post {
                        image.x = borderRect.centerX() - image.width / 2f
                        image.y = borderRect.centerY() - image.height / 2f
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    private fun setBestCropSize() {
        if (width >= height) {
            var bestHeight = height * 0.7f
            var bestWidth = bestHeight * aspectRatio
            while (bestWidth >= width) {
                bestHeight /= 2
                bestWidth = bestHeight * aspectRatio
            }
            cropWidth = bestWidth.roundToInt()
            cropHeight = bestHeight.roundToInt()
        } else {
            var bestWidth = width * 0.7f
            var bestHeight = bestWidth / aspectRatio
            while (bestHeight >= height) {
                bestWidth /= 2
                bestHeight = bestWidth / aspectRatio
            }
            cropWidth = bestWidth.roundToInt()
            cropHeight = bestHeight.roundToInt()
        }
    }

    private fun setupBorder() {
        val borderWidth = cropWidth + BorderView.BORDER_WIDTH
        val borderHeight = cropHeight + BorderView.BORDER_WIDTH
        val left = ((width - borderWidth) / 2f).roundToInt().toFloat()
        val top = ((height - borderHeight) / 2f).roundToInt().toFloat()
        val right = left + borderWidth
        val bottom = top + borderHeight
        border.setRect(RectF(left, top, right, bottom))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupRoot() {
        root.setOnTouchListener(gestureListener)
    }

    fun flipHorizontal() {
        image.scaleX *= -1
    }

    fun flipVertical() {
        image.scaleY *= -1
    }

    fun rotateForward() {
        image.rotation = (image.rotation + 90 + 360) % 360
    }

    fun rotateBackward() {
        image.rotation = (image.rotation - 90 + 360) % 360
    }

    fun fit() {
        val borderRect = border.getRect()
        val scale = if (image.rotation == 90f || image.rotation == 270f) {
            max(
                borderRect.width() / image.height,
                borderRect.height() / image.width
            )
        } else {
            max(
                borderRect.width() / image.width,
                borderRect.height() / image.height
            )
        }
        image.scaleX = scale * if (image.scaleX < 0) -1f else 1f
        image.scaleY = scale * if (image.scaleY < 0) -1f else 1f
        image.translationX = 0f
        image.translationY = 0f
        gestureListener.resetMatrix()
        image.post {
            image.x = (borderRect.centerX() - image.width / 2f)
            image.y = (borderRect.centerY() - image.height / 2f)
        }
    }

    fun getCropBitmap(): Bitmap {
        val bitmap = (image.drawable as? BitmapDrawable)?.bitmap ?: return Bitmap.createBitmap(
            outputWidth,
            outputHeight,
            Bitmap.Config.ARGB_8888
        )
        val borderRect = border.getRect()
        val output = Bitmap.createBitmap(
            outputWidth,
            outputHeight,
            Bitmap.Config.ARGB_8888
        )
        val imagePos = image.position
        Canvas(output).apply {
            drawColor(Color.WHITE)
            translate(
                (imagePos.x - borderRect.left) * (outputWidth / borderRect.width()),
                (imagePos.y - borderRect.top) * (outputHeight / borderRect.height())
            )
            scale(outputWidth / borderRect.width(), outputHeight / borderRect.height())
            scale(image.width / bitmap.width.toFloat(), image.height / bitmap.height.toFloat())
            scale(image.scaleX, image.scaleY)
            rotate(image.rotation)
            drawBitmap(bitmap, 0f, 0f, null)
        }
        return output
    }

    private val View.position: PointF
        get() {
            val points = floatArrayOf(x, y)
            val matrix = Matrix().apply {
                postScale(scaleX, scaleY, x + pivotX, y + pivotY)
                postRotate(rotation, x + pivotX, y + pivotY)
            }
            matrix.mapPoints(points)
            return PointF(points[0], points[1])
        }
}