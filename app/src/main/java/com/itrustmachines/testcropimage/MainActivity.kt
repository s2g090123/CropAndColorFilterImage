package com.itrustmachines.testcropimage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val widthEt by lazy { findViewById<EditText>(R.id.etWidth) }
    private val heightEt by lazy { findViewById<EditText>(R.id.etHeight) }
    private val startBtn by lazy { findViewById<Button>(R.id.start) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startBtn.setOnClickListener {
            val width = widthEt.text.toString().toIntOrNull() ?: return@setOnClickListener
            val height = heightEt.text.toString().toIntOrNull() ?: return@setOnClickListener
            val intent = Intent(this, CropActivity::class.java).apply {
                putExtra(CropActivity.INTENT_CROP_WIDTH, width)
                putExtra(CropActivity.INTENT_CROP_HEIGHT, height)
            }
            startActivity(intent)
        }
    }
}