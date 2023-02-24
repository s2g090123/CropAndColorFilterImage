package com.itrustmachines.testcropimage

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.lifecycleScope
import com.itrustmachines.testcropimage.compose.ColorScreen
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ColorActivity : ComponentActivity() {

    companion object {
        const val INTENT_IMAGE = "intent_image"
    }

    private val viewModel: ColorViewModel by viewModels()

    private val imagePath by lazy { intent.getStringExtra(INTENT_IMAGE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(
                LocalLayoutDirection provides LayoutDirection.Ltr,
                LocalTextStyle provides LocalTextStyle.current.copy(textDirection = TextDirection.Ltr)
            ) {
                ColorScreen(
                    viewModel = viewModel,
                    imagePath = imagePath ?: ""
                )
            }
        }

        lifecycleScope.launch {
            viewModel.saveToDownload.collect {
                saveImageToDownload(it)
            }
        }
    }

    private fun saveImageToDownload(bitmap: Bitmap?) {
        bitmap ?: return
        val outputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "image.png")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri =
                contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let { contentResolver.openOutputStream(uri) }
        } else {
            val file = File(Environment.DIRECTORY_DOWNLOADS, "image.png")
            FileOutputStream(file)
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(
                Uri.parse(Environment.DIRECTORY_DOWNLOADS),
                DocumentsContract.Document.MIME_TYPE_DIR
            )
        }
        startActivity(intent)
    }
}