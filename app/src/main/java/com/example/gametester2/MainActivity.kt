package com.example.gametester2

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var etDate: EditText
    private lateinit var etMinutesPlayed: EditText
    private lateinit var spinnerGenre: Spinner
    private lateinit var spinnerRating: Spinner
    private lateinit var btnAddEntry: Button
    private lateinit var btnClearInputs: Button
    private lateinit var btnGoToDetails: Button

    // Using a singleton-like approach for GameSessionProcessor to persist data across activities
    // In a real app, consider a ViewModel, SharedPreferences, or database for more robust state management.
    companion object {
        var gameSessionProcessor: GameSessionProcessor = GameSessionProcessor()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize views
        etDate = findViewById(R.id.etDate)
        etMinutesPlayed = findViewById(R.id.etMinutesPlayed)
        spinnerGenre = findViewById(R.id.spinnerGenre)
        spinnerRating = findViewById(R.id.spinnerRating)
        btnAddEntry = findViewById(R.id.btnAddEntry)
        btnClearInputs = findViewById(R.id.btnClearInputs)
        btnGoToDetails = findViewById(R.id.btnGoToDetails)

        // Set up Date Picker for etDate
        etDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Setup Spinners with data from strings.xml
        ArrayAdapter.createFromResource(
            this,
            R.array.game_genres,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGenre.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.ratings,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRating.adapter = adapter
        }

        // Set up Add Entry button click listener
        btnAddEntry.setOnClickListener {
            addGameEntry()
        }

        // Set up Clear Inputs button click listener
        btnClearInputs.setOnClickListener {
            clearInputFields()
        }

        // Set up Go to Details button click listener
        btnGoToDetails.setOnClickListener {
            val intent = Intent(this,detailed_view ::class.java)
            // Pass the processor object to the next activity
            intent.putExtra("gameSessionProcessor", gameSessionProcessor)
            startActivity(intent)
        }

        // Initial check for max entries
        updateAddEntryButtonState()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth)
                etDate.setText(date)
            }, year, month, day)
        datePickerDialog.show()
    }

    private fun addGameEntry() {
        val date = etDate.text.toString().trim()
        val minutesPlayedStr = etMinutesPlayed.text.toString().trim()
        val genre = spinnerGenre.selectedItem.toString()
        val ratingStr = spinnerRating.selectedItem.toString()

        // --- Error Handling & Validation ---
        if (date.isEmpty() || minutesPlayedStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val minutes: Int
        try {
            minutes = minutesPlayedStr.toInt()
            if (minutes <= 0) {
                Toast.makeText(this, "Minutes Played must be a positive number.", Toast.LENGTH_SHORT).show()
                return
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Minutes Played must be a valid number.", Toast.LENGTH_SHORT).show()
            return
        }

        val rating: Int
        try {
            rating = ratingStr.toInt()
            if (rating < 1 || rating > 5) {
                Toast.makeText(this, "Rating must be between 1 and 5.", Toast.LENGTH_SHORT).show()
                return
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Invalid rating selected.", Toast.LENGTH_SHORT).show()
            return
        }
        // --- End Error Handling ---

        // Add entry using GameSessionProcessor
        val added = gameSessionProcessor.addEntry(date, minutes, genre, rating)

        if (added) {
            Toast.makeText(this, "Entry added successfully! Total entries: ${gameSessionProcessor.entryCount}", Toast.LENGTH_SHORT).show()
            clearInputFields()
            updateAddEntryButtonState()
        } else {
            Toast.makeText(this, "Maximum 7 entries reached. Cannot add more.", Toast.LENGTH_LONG).show()
            btnAddEntry.isEnabled = false // Disable button if max is reached
        }
    }

    private fun clearInputFields() {
        etDate.setText("")
        etMinutesPlayed.setText("")
        spinnerGenre.setSelection(0) // Select first item in genre spinner
        spinnerRating.setSelection(0) // Select first item in rating spinner
    }

    private fun updateAddEntryButtonState() {
        if (gameSessionProcessor.entryCount >= 7) {
            btnAddEntry.isEnabled = false
            Toast.makeText(this, "Maximum 7 entries reached.", Toast.LENGTH_LONG).show()
        } else {
            btnAddEntry.isEnabled = true
        }
    }

    // This override is important if you want to update the button state when returning from details screen
    override fun onResume() {
        super.onResume()
        updateAddEntryButtonState()
    }
}