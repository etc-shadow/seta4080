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

class FinishScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_screen)
        animateBackground()
        val score = intent.getIntExtra("EXTRA_USER_SCORE", 0).toInt()
        val possibleScore = intent.getIntExtra("EXTRA_HIGHEST_POSSIBLE_SCORE", 0).toInt()

        // Assign image/level based on score. 90% gets gold, 80% silver, 60% bronze, all others simply state "completed"
        if (possibleScore == 0) {
            // No quiz options existed. Automatic gold.
            setScore(5)
        } else if (score.div(possibleScore) > 0.9) {
            setScore(1)
        } else if (score.div(possibleScore) > 0.8) {
            setScore(2)
        } else if (score.div(possibleScore) > 0.7) {
            setScore(3)
        } else {
            setScore(4)
        }
    }

    /* Called to activate background dynamic gradient - it's a fun UI to keep eyes on the screen*/
    fun animateBackground(){
        val container = findViewById<ConstraintLayout>(R.id.mainLayout)
        val backgroundFlow = container.background as AnimationDrawable
        backgroundFlow.setEnterFadeDuration(2000)
        backgroundFlow.setExitFadeDuration(4000)
        backgroundFlow.start()
    }

    /* Called by "next" button, returns information to activity caller and closes activity*/
    fun concludeModule(view: View) {
        val resultIntent = Intent(applicationContext, MainActivity::class.java).apply {
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

    /* Called to update trophies and messages*/
    fun setScore(level: Int){
        // Level 1 is gold, 2 is silver, 3 is bronze, 4 is "completion", 5 is no quiz expected
        val textbox = findViewById<TextView>(R.id.message_box)
        val trophyBox = findViewById<ImageView>(R.id.trophy_box)
        if (level == 1){
            textbox.text = "Congratulations!\nYou've completed this course with a score above 90%!\nKeep an eye on your email for your gold badge!"
            trophyBox.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trophy_gold))
        } else if (level == 2){
            textbox.text = "Congratulations!\nYou've completed this course with a score above 80%!\nKeep an eye on your email for your silver badge!"
            trophyBox.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trophy_silver))
        } else if (level == 3){
            textbox.text = "Congratulations!\nYou've completed this course with a score above 70%!\nKeep an eye on your email for your bronze badge!"
            trophyBox.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trophy_bronze))
        } else if (level == 4){
            textbox.text = "Congratulations!\nYou've completed this course!\nWhen you're ready, you can retry this module for your chance at a badge!"
            trophyBox.setImageDrawable(null)
        } else if (level == 5){
            textbox.text = "Congratulations!\nYou've completed this module! Keep an eye on your email for a special badge for covering this material!"
            trophyBox.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trophy_gold))
        }
    }
}