package com.pk.jamalzahid.faultspy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoggerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_logger)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadViews(intent)
    }

    fun loadViews(intent: Intent){
        val details = intent.getStringExtra(Constants.EXTRA_DETAILS)
        findViewById<TextView>(R.id.txtDetails)
            .text = details
        findViewById<Button>(R.id.btnClose)
            .setOnClickListener {
                finishAndRemoveTask()
                System.exit(0)
            }
    }
}