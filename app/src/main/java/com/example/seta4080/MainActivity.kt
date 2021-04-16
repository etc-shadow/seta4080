package com.example.seta4080

import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import java.io.File
import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    /* Initialize class variables*/
    // Track activity, points
    var questionIndex = 0 // Tracks where we are in the activities list for this module
    var points = 0 // Tracks points accumulated in this module
    var pointsPossible = 0 // Tracks max points possible in this module. Consider tracking incorrect instead and using sum
    // Status codes
    val SETA_MC_REQUEST_CODE = 101
    val SETA_TEXT_REQUEST_CODE = 102
    val SETA_MODULE_REQUEST_CODE = 300
    // List of relevant activities
    var concepts: Array<String>? = null
    // Database Connection Object
    lateinit var DB_CONNECTION: DBTools

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupMainScreen()
        DB_CONNECTION = DBTools(this)
        DB_CONNECTION.copyDbToDevice(this)
    }

    fun findLearningPath(){
        val intent = Intent(this, SelectModule::class.java)
        startActivityForResult(intent, SETA_MODULE_REQUEST_CODE)
    }

    fun animateBackground(){
        val container = findViewById<ConstraintLayout>(R.id.activity_layout)
        val backgroundFlow = container.background as AnimationDrawable
        backgroundFlow.setEnterFadeDuration(2000)
        backgroundFlow.setExitFadeDuration(4000)
        backgroundFlow.start()
    }

    fun initLearningPath(learningPathType:String){
        when (learningPathType){
            "networks_path" -> {
                concepts = getString(R.string.NETWORKS_EASY).split(",").toTypedArray()
            }

        }
        checkMaxPointsPossible()
        displayScreen(questionIndex)
    }

    fun checkMaxPointsPossible(){
        var numOfQuizzes = 0
        concepts?.forEach { i ->
            if (i.split("-").get(0).equals("quiz")){
                numOfQuizzes++
            }
        }
        pointsPossible = numOfQuizzes
    }

    fun setupMainScreen(){
        setContentView(R.layout.activity_main)
        animateBackground()
        val usernameFileExists = File(this.filesDir, "seta_username_file").exists()
        var username = ""
        if (usernameFileExists) {
            username = this.openFileInput("seta_username_file").bufferedReader().readText()
        }
        if (username.isNullOrBlank()){
            findViewById<TextView>(R.id.mainTextView).text = "Welcome to the Security Education Training and Awareness App"
        } else {
            findViewById<TextView>(R.id.UserNamePrompt).text = username
            findViewById<TextView>(R.id.UserNamePrompt).visibility = View.GONE
            findViewById<TextView>(R.id.mainTextView).text = "Welcome back to SETA, $username"
        }
    }

    fun startAppFlow(view: View){
        findLearningPath()
    }

    fun updateUsername(){
        val inputUserName = findViewById<TextView>(R.id.UserNamePrompt).text.toString()
        this.openFileOutput("seta_username_file", Context.MODE_PRIVATE).use {
            it.write(inputUserName.toByteArray())
        }
    }

    /* Update multiple choice quiz screen with information*/
    fun updateMCQuestions(title: String, question: String, options: Array<String>, solution: String) {
        // Call intent
        val intent = Intent(this, MCQuiz::class.java).apply{ putExtra("EXTRA_MC_TITLE", title)
            putExtra("EXTRA_MC_QUESTION", question)
            putExtra("EXTRA_MC_OPTIONS", options)
            putExtra("EXTRA_MC_ANSWER", solution)}
        startActivityForResult(intent, SETA_MC_REQUEST_CODE)
    }

    /* Results from activities are returned here. We use different request codes to call different activities (check which activity returned,
    * and that it belonged to our app)
    * We can also check the returned Intent "data" for information the activities pass back
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // If MC returns successfully
        if (resultCode == Activity.RESULT_OK && requestCode == SETA_MC_REQUEST_CODE) {
            if (data?.getStringExtra("BUTTON_STATUS").equals("NEXT")){
                // If user clicked next, increment index and award point if correct
                if (data?.getBooleanExtra("MC_USER_CORRECT", false) == true){
                    points++
                }
                questionIndex++
                displayScreen(questionIndex)
            } else if (data?.getStringExtra("BUTTON_STATUS").equals("PREV")) {
                // If user clicked back, return to previous (at least index 0)
                questionIndex = max(0, questionIndex-1)
                displayScreen(questionIndex)
            }
        // Otherwise if TEXT returns successfully
        } else if (resultCode == Activity.RESULT_OK && requestCode == SETA_TEXT_REQUEST_CODE) {
            if (data?.getStringExtra("BUTTON_STATUS").equals("NEXT")){
                // If user clicked next, increment index
                questionIndex++
                displayScreen(questionIndex)
            } else if (data?.getStringExtra("MC_BUTTON_STATUS").equals("PREV")) {
                // If user clicked back, return to previous (at least index 0)
                questionIndex = max(0, questionIndex-1)
                displayScreen(questionIndex)
            }
        // Otherwise if it's our module selection page returning
        } else if (resultCode == Activity.RESULT_OK && requestCode == SETA_MODULE_REQUEST_CODE) {
            initLearningPath(data?.getStringExtra("EXTRA_LEARNING_PATH").toString())
        }
    }

    /* Single function to proceed to the next relevant screen. Uses the class var "questionIndex" to track where in the array of module activities we are/should be
    *
    */
    fun displayScreen(index: Int) {
        if (index >= concepts?.size!!){
            // Index exceeded array, module complete
            if (pointsPossible == 0){
                Toast.makeText(this,"Great work! You've completed this module.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "You got $points out of $pointsPossible", Toast.LENGTH_LONG).show()
            }

            // Reset values before returning
            points=0
            pointsPossible=0
            questionIndex=0
            setupMainScreen()
        } else {
            val current = concepts?.get(index).toString()
            val currentType = current?.split("-")?.get(0)
            val currentEntryRow = current?.split("-")?.get(1)?.toInt()
            if (currentType.equals("quiz")) {
                if (currentEntryRow != null) {
                    val entryData = DB_CONNECTION.readDB("quiz", currentEntryRow)
                    val mc_title = entryData["title"].toString()
                    val mc_question = entryData["question"].toString()
                    val mc_options = entryData["options"].toString().split(";").toTypedArray()
                    val mc_solution = entryData["solution"].toString()
                    updateMCQuestions(mc_title, mc_question, mc_options, mc_solution)
                }
            } else if (currentType.equals("textInfo")) {
                if (currentEntryRow != null) {
                    val entryData = DB_CONNECTION.readDB("textInfo", currentEntryRow)
                    val text_title = entryData["title"].toString()
                    val text_info = entryData["textInfo"].toString()
                    updateTextBox(text_title, text_info)
                }
            }
        }
    }

    fun updateMCwithImage(title: String, question: String, imgLink: String, options: List<String>){
        // Update UI
        //setContentView(R.layout.MCImage)
        // Update text boxes (question + 4 buttons)
        findViewById<TextView>(R.id.MCTitle).setText(title)
        findViewById<TextView>(R.id.MCQuestionBox).setText(question)
        findViewById<Button>(R.id.mc_option1).setText(options[0])
        findViewById<Button>(R.id.mc_option2).setText(options[1])
        findViewById<Button>(R.id.mc_option3).setText(options[2])
        findViewById<Button>(R.id.mc_option4).setText(options[3])
        // Update image
        //TODO: findViewById<Image>(R.id.imageFrame).imageSrc(imgLink???)
    }
    fun updateTextBox(title: String, text: String){
        // Call intent
        val intent = Intent(this, TextInfo::class.java).apply{ putExtra("EXTRA_TEXT_TITLE", title)
            putExtra("EXTRA_TEXT_INFO", text)}
        startActivityForResult(intent, SETA_TEXT_REQUEST_CODE)
    }
}