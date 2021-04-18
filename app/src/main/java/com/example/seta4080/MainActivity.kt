package com.example.seta4080

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import java.io.File
import kotlin.math.max

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
    val SETA_FINISH_SCREEN_REQUEST_CODE = 500
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

    /* Called by the start button on the main page, this function has us display the modules menu*/
    fun userSelectLearningPath(){
        val intent = Intent(this, SelectModule::class.java)
        startActivityForResult(intent, SETA_MODULE_REQUEST_CODE)
    }

    /* Called to activate background dynamic gradient - it's a fun UI to keep eyes on the screen*/
    fun animateBackground(){
        val container = findViewById<ConstraintLayout>(R.id.activity_layout)
        val backgroundFlow = container.background as AnimationDrawable
        backgroundFlow.setEnterFadeDuration(2000)
        backgroundFlow.setExitFadeDuration(4000)
        backgroundFlow.start()
    }

    /* Pull learning path (called after user selects a module) and update it as variable for this session. We will track our progress on the array*/
    fun initLearningPath(learningPathType:String){
        when (learningPathType){
            "networks_path" -> {
                concepts = getString(R.string.NETWORKS_EASY).split(",").toTypedArray()
            }

        }
        checkMaxPointsPossible()
        displayScreen(questionIndex)
    }

    /* Examine the current learning path and look for all quizzes*/
    fun checkMaxPointsPossible(){
        var numOfQuizzes = 0
        concepts?.forEach { i ->
            if (i.split("-").get(0).equals("quiz")){
                numOfQuizzes++
            }
        }
        pointsPossible = numOfQuizzes
    }

    /* Home screen setup*/
    fun setupMainScreen(){
        setContentView(R.layout.activity_main)
        animateBackground()
        // Grab local username file
        val usernameFileExists = File(this.filesDir, "seta_username_file").exists()
        var username = ""
        if (usernameFileExists) {
            username = this.openFileInput("seta_username_file").bufferedReader().readText()
        }
        // We don't have a record of this user, display standard message and wait for them to provide name
        if (username.isNullOrBlank()){
            findViewById<TextView>(R.id.mainTextView).text = "Welcome to the Security Education Training and Awareness App\n(SETA)"
        } else {
            // Otherwise, user has visited the app before and we welcome them back
            findViewById<TextView>(R.id.UserNamePrompt).text = username
            findViewById<TextView>(R.id.UserNamePrompt).visibility = View.GONE
            findViewById<TextView>(R.id.mainTextView).text = "Welcome back to SETA, $username"
        }
    }

    /* Standard start procedure (update username, prompt user for learning path*/
    fun startAppFlow(view: View){
        updateUsername()
        userSelectLearningPath()
    }

    /* Write username to local storage file*/
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
        // Otherwise if our finish screen returning
        } else if (resultCode == Activity.RESULT_OK && requestCode == SETA_FINISH_SCREEN_REQUEST_CODE) {
            // Reset values before returning to main
            points = 0
            pointsPossible = 0
            questionIndex = 0
            userSelectLearningPath()
        }
    }

    /* Single function to proceed to the next relevant screen. Uses the class var "questionIndex" to track where in the array of module activities we are/should be*/
    fun displayScreen(index: Int) {
        if (index >= concepts?.size!!){
            // Index exceeded array, module complete

            /*if (pointsPossible == 0){
                Toast.makeText(this,"Great work! You've completed this module.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "You got $points out of $pointsPossible", Toast.LENGTH_LONG).show()
            }*/
            // Index exceeded array, module complete
            displayFinishScreen()
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

    fun displayFinishScreen(){
        val intent = Intent(this, FinishScreen::class.java).apply {
            putExtra("EXTRA_USER_SCORE", points)
            putExtra("EXTRA_HIGHEST_POSSIBLE_SCORE", pointsPossible)
        }
        startActivityForResult(intent, SETA_FINISH_SCREEN_REQUEST_CODE)
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