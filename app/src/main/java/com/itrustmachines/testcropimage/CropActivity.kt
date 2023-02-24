package com.itrustmachines.testcropimage

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.itrustmachines.testcropimage.widget.CropView
import java.io.File
import java.io.FileOutputStream

class CropActivity : AppCompatActivity() {

    companion object {
        const val INTENT_CROP_WIDTH = "intent_crop_width"
        const val INTENT_CROP_HEIGHT = "intent_crop_height"
    }

    private val crop by lazy { findViewById<CropView>(R.id.crop) }
    private val saveBtn by lazy { findViewById<ImageView>(R.id.iv_next) }
    private val flipHorizontal by lazy { findViewById<ImageView>(R.id.iv_horizontalFlip) }
    private val flipVertical by lazy { findViewById<ImageView>(R.id.iv_verticalFlip) }
    private val rotateForward by lazy { findViewById<ImageView>(R.id.iv_rotateRight) }
    private val rotateBackward by lazy { findViewById<ImageView>(R.id.iv_rotateLeft) }
    private val fit by lazy { findViewById<ImageView>(R.id.iv_fit) }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val cropWidth = intent.getIntExtra(INTENT_CROP_WIDTH, 1920)
                val cropHeight = intent.getIntExtra(INTENT_CROP_HEIGHT, 1440)
                crop.setImageUri(uri, cropWidth, cropHeight)
            } else {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)
        pickMedia.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )

        saveBtn.setOnClickListener {
            val file = saveBitmap(crop.getCropBitmap())
            startActivity(
                Intent(this, ColorActivity::class.java).apply {
                    putExtra(ColorActivity.INTENT_IMAGE, file.absolutePath)
                }
            )
        }
        flipHorizontal.setOnClickListener {
            crop.flipHorizontal()
        }
        flipVertical.setOnClickListener {
            crop.flipVertical()
        }
        rotateForward.setOnClickListener {
            crop.rotateForward()
        }
        rotateBackward.setOnClickListener {
            crop.rotateBackward()
        }
        fit.setOnClickListener {
            crop.fit()
        }
    }

    private fun saveBitmap(bitmap: Bitmap): File {
        val folder = externalCacheDir
        val file = File(folder, "test.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }
}