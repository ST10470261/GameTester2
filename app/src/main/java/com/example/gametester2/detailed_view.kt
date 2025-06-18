package com.example.gametester2

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.system.exitProcess

class detailed_view : AppCompatActivity() {
    private lateinit var lvGameEntries: ListView
    private lateinit var tvAverageMinutes: TextView
    private lateinit var tvHighestRatedGame: TextView
    private lateinit var tvTotalSessions: TextView
    private lateinit var btnBackToMain: Button
    private lateinit var btnExitApp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detailed_view)

        // Initialize views
        lvGameEntries = findViewById(R.id.lvGameEntries)
        tvAverageMinutes = findViewById(R.id.tvAverageMinutes)
        tvHighestRatedGame = findViewById(R.id.tvHighestRatedGame)
        tvTotalSessions = findViewById(R.id.tvTotalSessions)
        btnBackToMain = findViewById(R.id.btnBackToMain)
        btnExitApp = findViewById(R.id.btnExitApp)

        // Retrieve the GameSessionProcessor object from the Intent
        val gameSessionProcessor = intent.getParcelableExtra<GameSessionProcessor>("gameSessionProcessor")

        if (gameSessionProcessor != null) {
            // Display all recorded entries in the ListView
            val formattedEntries = gameSessionProcessor.generateFormattedEntries()
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, formattedEntries)
            lvGameEntries.adapter = adapter

            // Display calculated values
            val avgMinutes = String.format("%.2f", gameSessionProcessor.calculateAverageMinutes())
            tvAverageMinutes.text = "Average minutes played per day: $avgMinutes min"

            val highestRated = gameSessionProcessor.findHighestRatedGame()
            tvHighestRatedGame.text = "Highest-rated game: ${highestRated.first} (Rating: ${highestRated.second}/5)"

            tvTotalSessions.text = "Total number of play sessions: ${gameSessionProcessor.entryCount}"

        } else {
            // Handle case where processor object is null (shouldn't happen with correct flow)
            tvAverageMinutes.text = "Average minutes played per day: N/A"
            tvHighestRatedGame.text = "Highest-rated game: N/A"
            tvTotalSessions.text = "Total number of play sessions: 0"
            lvGameEntries.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listOf("Error: No data to display."))
        }


        // Set up Back to Main Screen button click listener
        btnBackToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Using FLAG_ACTIVITY_CLEAR_TOP and FLAG_ACTIVITY_SINGLE_TOP to ensure we return to an existing MainActivity instance
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish() // Finish this activity to remove it from the back stack
        }

        // Set up Exit App button click listener
        btnExitApp.setOnClickListener {
            finishAffinity() // Closes all activities in the task associated with this activity
            exitProcess(0)
        }
    }
}