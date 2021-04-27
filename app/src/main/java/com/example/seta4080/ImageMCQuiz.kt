package com.example.seta4080

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class ImageMCQuiz : AppCompatActivity() {
    var userAnswer: String = ""
    var solution: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Update UI
        setContentView(R.layout.activity_image_mc_quiz)
        animateBackground()

        // Update text boxes (title + question + 4 buttons)
        findViewById<TextView>(R.id.ImageMCTitle).text = intent.getStringExtra("EXTRA_MC_TITLE")
        findViewById<TextView>(R.id.ImageMCQuestionBox).text = intent.getStringExtra("EXTRA_MC_QUESTION")
        val options = intent.getStringArrayExtra("EXTRA_MC_OPTIONS")
        findViewById<RadioButton>(R.id.image_mc_option1).text = options?.get(0)
        findViewById<RadioButton>(R.id.image_mc_option2).text = options?.get(1)
        findViewById<RadioButton>(R.id.image_mc_option3).text = options?.get(2)
        findViewById<RadioButton>(R.id.image_mc_option4).text = options?.get(3)
        // Accessibility features for buttons
        findViewById<RadioButton>(R.id.image_mc_option1).contentDescription = options?.get(0)
        findViewById<RadioButton>(R.id.image_mc_option2).contentDescription = options?.get(1)
        findViewById<RadioButton>(R.id.image_mc_option3).contentDescription = options?.get(2)
        findViewById<RadioButton>(R.id.image_mc_option4).contentDescription = options?.get(3)
        // Pull answer from Intent
        solution = intent.getStringExtra("EXTRA_MC_ANSWER").toString()

        // Update display image
        val imageId = "com.example.seta4080:drawable/"+intent.getStringExtra("EXTRA_MC_IMAGE")
        val resouceId = this.resources.getIdentifier(imageId, "id", this.packageName)
        findViewById<ImageView>(R.id.MCImageView).setImageDrawable(ContextCompat.getDrawable(this, resouceId))
    }

    /* Called whenever option selected, updates UI and enables "next" button*/
    fun mcSelected(view: View) {
        val option1 = findViewById<RadioButton>(R.id.image_mc_option1)
        val option2 = findViewById<RadioButton>(R.id.image_mc_option2)
        val option3 = findViewById<RadioButton>(R.id.image_mc_option3)
        val option4 = findViewById<RadioButton>(R.id.image_mc_option4)
        val mcNextButton: Button = findViewById<Button>(R.id.ImageMCNextButton)
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.id){
                R.id.image_mc_option1 -> if (checked){
                    userAnswer = option1.text as String
                    option1.setBackgroundColor(Color.GREEN)
                    mcNextButton.isEnabled = true
                    mcNextButton.setTextColor(Color.WHITE)
                    option2.setBackgroundColor(Color.parseColor("#9C9696"))
                    option3.setBackgroundColor(Color.parseColor("#9C9696"))
                    option4.setBackgroundColor(Color.parseColor("#9C9696"))
                }
                R.id.image_mc_option2 -> if (checked){
                    userAnswer = option2.text as String
                    option2.setBackgroundColor(Color.GREEN)
                    mcNextButton.isEnabled = true
                    mcNextButton.setTextColor(Color.WHITE)
                    option1.setBackgroundColor(Color.parseColor("#9C9696"))
                    option3.setBackgroundColor(Color.parseColor("#9C9696"))
                    option4.setBackgroundColor(Color.parseColor("#9C9696"))
                }
                R.id.image_mc_option3 -> if (checked){
                    userAnswer = option3.text as String
                    option3.setBackgroundColor(Color.GREEN)
                    mcNextButton.isEnabled = true
                    mcNextButton.setTextColor(Color.WHITE)
                    option1.setBackgroundColor(Color.parseColor("#9C9696"))
                    option2.setBackgroundColor(Color.parseColor("#9C9696"))
                    option4.setBackgroundColor(Color.parseColor("#9C9696"))
                }
                R.id.image_mc_option4 -> if (checked){
                    userAnswer = option4.text as String
                    option4.setBackgroundColor(Color.GREEN)
                    mcNextButton.isEnabled = true
                    mcNextButton.setTextColor(Color.WHITE)
                    option1.setBackgroundColor(Color.parseColor("#9C9696"))
                    option2.setBackgroundColor(Color.parseColor("#9C9696"))
                    option3.setBackgroundColor(Color.parseColor("#9C9696"))
                }
            }
        }
    }

    /* Called by "next" button, returns information to activity caller and closes activity*/
    fun submitAnswer(view: View) {
        val correctAnswer:Boolean = userAnswer.equals(solution)
        val resultIntent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("MC_USER_CORRECT", correctAnswer)
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