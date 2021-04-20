package com.example.seta4080

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

lateinit var YT_PLAYER : WebView
lateinit var YT_VIDEO_ID : String
class VideoPlayback : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_playback)
        animateBackground()
        val YT_VIDEO_TITLE = intent.getStringExtra("EXTRA_YT_TITLE")
        YT_VIDEO_ID = "https://youtube.com/embed/" + intent.getStringExtra("EXTRA_YT_EMBED_CODE")
        YT_PLAYER = findViewById<WebView>(R.id.videoCaller)
        YT_PLAYER.apply {
            settings.javaScriptEnabled = true
            webChromeClient = WebChromeClient()
        }
        findViewById<TextView>(R.id.videoTextView).text = "We're about to launch a YouTube video! Press play to watch \"" + YT_VIDEO_TITLE + "\"\nAfterwards, simply double tap the back button to return to this page"
    }

    /* Called by the next button, returns a successful message to caller with which button called*/
    fun proceed(view: View) {
        val resultIntent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("BUTTON_STATUS", "NEXT")
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

    /* Called by the play button, the webview triggers the YouTube app*/
    fun playVideo(view: View) {
        // In modern Android, calling a YouTube link through this configuration launches the YouTube app
        YT_PLAYER.loadUrl(YT_VIDEO_ID)
        YT_PLAYER.webChromeClient = WebChromeClient()
    }

    /* Called to activate background dynamic gradient - it's a fun UI to keep eyes on the screen*/
    fun animateBackground(){
        val container = findViewById<ConstraintLayout>(R.id.activity_layout)
        val backgroundFlow = container.background as AnimationDrawable
        backgroundFlow.setEnterFadeDuration(2000)
        backgroundFlow.setExitFadeDuration(4000)
        backgroundFlow.start()
    }
}