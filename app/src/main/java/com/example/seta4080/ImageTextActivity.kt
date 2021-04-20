package com.example.seta4080

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import org.intellij.lang.annotations.Identifier

class ImageTextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Update UI
        setContentView(R.layout.activity_image_text)
        animateBackground()
        // Update title and text
        findViewById<TextView>(R.id.imageTextInfoTitle).text = intent.getStringExtra("EXTRA_IMAGE_TEXT_TITLE")
        findViewById<TextView>(R.id.imageTextInfoBlock).text = intent.getStringExtra("EXTRA_IMAGE_TEXT_INFO")
        // Update display image
        val imageId = "com.example.seta4080:drawable/"+intent.getStringExtra("EXTRA_IMAGE_TEXT_IMAGE")
        val resouceId = this.resources.getIdentifier(imageId, "id", this.packageName)
        print(imageId)
        print(resouceId)
        findViewById<ImageView>(R.id.textImageView).setImageDrawable(ContextCompat.getDrawable(this, resouceId))
    }

    fun submitAnswer(view: View) {
        val resultIntent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("BUTTON_STATUS", "NEXT")
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

    fun animateBackground(){
        val container = findViewById<ConstraintLayout>(R.id.mainLayout)
        val backgroundFlow = container.background as AnimationDrawable
        backgroundFlow.setEnterFadeDuration(2000)
        backgroundFlow.setExitFadeDuration(4000)
        backgroundFlow.start()
    }
}