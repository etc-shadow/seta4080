package com.example.seta4080

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class MCQuiz : AppCompatActivity() {
    var userAnswer: String = ""
    var solution: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Update UI
        setContentView(R.layout.activity_mc_quiz)
        animateBackground()

        // Update text boxes (title + question + 4 buttons)
        findViewById<TextView>(R.id.MCTitle).text = intent.getStringExtra("EXTRA_MC_TITLE")
        findViewById<TextView>(R.id.MCQuestionBox).text = intent.getStringExtra("EXTRA_MC_QUESTION")
        val options = intent.getStringArrayExtra("EXTRA_MC_OPTIONS")
        findViewById<RadioButton>(R.id.mc_option1).text = options?.get(0)
        findViewById<RadioButton>(R.id.mc_option2).text = options?.get(1)
        findViewById<RadioButton>(R.id.mc_option3).text = options?.get(2)
        findViewById<RadioButton>(R.id.mc_option4).text = options?.get(3)
        // Pull answer from Intent
        solution = intent.getStringExtra("EXTRA_MC_ANSWER").toString()
    }

    fun mcSelected(view: View) {
        val option1 = findViewById<RadioButton>(R.id.mc_option1)
        val option2 = findViewById<RadioButton>(R.id.mc_option2)
        val option3 = findViewById<RadioButton>(R.id.mc_option3)
        val option4 = findViewById<RadioButton>(R.id.mc_option4)
        val mcNextButton: Button = findViewById<Button>(R.id.MCNextButton)
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.id){
                R.id.mc_option1 -> if (checked){
                    userAnswer = option1.text as String
                    option1.setBackgroundColor(Color.GREEN)
                    mcNextButton.isEnabled = true
                    mcNextButton.setTextColor(Color.WHITE)
                    option2.setBackgroundColor(Color.parseColor("#9C9696"))
                    option3.setBackgroundColor(Color.parseColor("#9C9696"))
                    option4.setBackgroundColor(Color.parseColor("#9C9696"))
                }
                R.id.mc_option2 -> if (checked){
                    userAnswer = option2.text as String
                    option2.setBackgroundColor(Color.GREEN)
                    mcNextButton.isEnabled = true
                    mcNextButton.setTextColor(Color.WHITE)
                    option1.setBackgroundColor(Color.parseColor("#9C9696"))
                    option3.setBackgroundColor(Color.parseColor("#9C9696"))
                    option4.setBackgroundColor(Color.parseColor("#9C9696"))
                }
                R.id.mc_option3 -> if (checked){
                    userAnswer = option3.text as String
                    option3.setBackgroundColor(Color.GREEN)
                    mcNextButton.isEnabled = true
                    mcNextButton.setTextColor(Color.WHITE)
                    option1.setBackgroundColor(Color.parseColor("#9C9696"))
                    option2.setBackgroundColor(Color.parseColor("#9C9696"))
                    option4.setBackgroundColor(Color.parseColor("#9C9696"))
                }
                R.id.mc_option4 -> if (checked){
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

    fun submitAnswer(view: View) {
        val correctAnswer:Boolean = userAnswer.equals(solution)
        val resultIntent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("MC_USER_CORRECT", correctAnswer)
            putExtra("BUTTON_STATUS", "NEXT")
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

    fun animateBackground(){
        val container = findViewById<ConstraintLayout>(R.id.activity_layout)
        val backgroundFlow = container.background as AnimationDrawable
        backgroundFlow.setEnterFadeDuration(2000)
        backgroundFlow.setExitFadeDuration(4000)
        backgroundFlow.start()
    }
}
