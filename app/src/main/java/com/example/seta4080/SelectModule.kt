package com.example.seta4080

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout

class SelectModule : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_module)
        animateBackground()
    }

    /* Called by "next" button, returns path information to activity caller and closes activity*/
    fun selectLearningPath(view: View){
        val learningPathType = view.getTag().toString()
        val selectedModuleIntent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("EXTRA_LEARNING_PATH", learningPathType)
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

    /* Called by buttons for modules under development*/
    fun informVoid(view: View) {
        Toast.makeText(this, "That module is coming soon!", Toast.LENGTH_SHORT).show()
    }
}