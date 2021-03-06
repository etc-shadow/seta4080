package com.example.seta4080

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class TextInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Update UI
        setContentView(R.layout.activity_text_info)
        animateBackground()
        // Update title and text
        findViewById<TextView>(R.id.textInfoTitle).text = intent.getStringExtra("EXTRA_TEXT_TITLE")
        findViewById<TextView>(R.id.textInfoBlock).text = intent.getStringExtra("EXTRA_TEXT_INFO")
    }

    /* Called by "next" button, returns information to activity caller and closes activity*/
    fun submitAnswer(view: View) {
        val resultIntent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("BUTTON_STATUS", "NEXT")
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

    /* Called to activate background dynamic gradient - it's a fun UI to keep eyes on the screen*/
    fun animateBackground(){
        val container = findViewById<ConstraintLayout>(R.id.mainLayout)
        val backgroundFlow = container.background as AnimationDrawable
        backgroundFlow.setEnterFadeDuration(2000)
        backgroundFlow.setExitFadeDuration(4000)
        backgroundFlow.start()
    }
}