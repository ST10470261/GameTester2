package com.example.gametester2
import android.os.Parcel
import android.os.Parcelable

class GameSessionProcessor() : Parcelable {
    // Parallel 1D arrays to store game session data
    private val MAX_ENTRIES = 7
    private val dates: Array<String?> = arrayOfNulls(MAX_ENTRIES)
    private val minutesPlayed: Array<Int?> = arrayOfNulls(MAX_ENTRIES)
    private val gameGenres: Array<String?> = arrayOfNulls(MAX_ENTRIES)
    private val ratings: Array<Int?> = arrayOfNulls(MAX_ENTRIES)

    var entryCount: Int = 0

    // Constructor for Parcelable
    constructor(parcel: Parcel) : this() {
        parcel.readStringArray(dates)
        parcel.readArray(minutesPlayed::class.java.classLoader)?.let {
            for (i in it.indices) minutesPlayed[i] = it[i] as? Int
        }
        parcel.readStringArray(gameGenres)
        parcel.readArray(ratings::class.java.classLoader)?.let {
            for (i in it.indices) ratings[i] = it[i] as? Int
        }
        entryCount = parcel.readInt()
    }

    /**
     * Adds a new game session entry to the arrays.
     * @return True if entry was added successfully, false if max entries reached.
     */
    fun addEntry(date: String, minutes: Int, genre: String, rating: Int): Boolean {
        if (entryCount < MAX_ENTRIES) {
            dates[entryCount] = date
            minutesPlayed[entryCount] = minutes
            gameGenres[entryCount] = genre
            ratings[entryCount] = rating
            entryCount++
            return true
        }
        return false
    }

    /**
     * Calculates the average minutes played per day.
     * @return The average minutes played, or 0.0 if no entries.
     */
    fun calculateAverageMinutes(): Double {
        if (entryCount == 0) return 0.0
        var totalMinutes = 0
        var actualEntries = 0 // Count only valid, non-null minutes
        for (i in 0 until entryCount) {
            minutesPlayed[i]?.let {
                totalMinutes += it
                actualEntries++
            }
        }
        // Ensure we divide by actual number of entries with minutes logged
        return if (actualEntries > 0) totalMinutes.toDouble() / actualEntries else 0.0
    }

    /**
     * Finds the highest-rated game and its genre.
     * @return A Pair of (Game Genre, Highest Rating) or ("N/A", 0) if no entries.
     */
    fun findHighestRatedGame(): Pair<String, String> {
        if (entryCount == 0) return Pair("N/A", "N/A")

        var highestRating = 0
        var highestRatedGameGenre = "N/A"
        var highestRatedGameDate = "N/A"

        for (i in 0 until entryCount) {
            ratings[i]?.let { currentRating ->
                if (currentRating > highestRating) {
                    highestRating = currentRating
                    gameGenres[i]?.let { highestRatedGameGenre = it }
                    dates[i]?.let { highestRatedGameDate = it }
                }
            }
        }
        // Return genre and rating as a string for display
        return Pair(highestRatedGameGenre, highestRating.toString())
    }

    /**
     * Generates a formatted string for all recorded entries.
     * @return A list of strings, each representing a formatted game session entry.
     */
    fun generateFormattedEntries(): List<String> {
        val formattedList = mutableListOf<String>()
        if (entryCount == 0) {
            formattedList.add("No game sessions recorded yet.")
            return formattedList
        }

        for (i in 0 until entryCount) {
            val date = dates[i] ?: "N/A"
            val minutes = minutesPlayed[i]?.toString() ?: "N/A"
            val genre = gameGenres[i] ?: "N/A"
            val rating = ratings[i]?.toString() ?: "N/A"

            val feedback = when (genre) {
                "Action" -> "High-octane fun!"
                "Adventure" -> "Explore new worlds!"
                "Puzzle" -> "A true brain-teaser!"
                "Strategy" -> "Think before you act!"
                "Platformer" -> "Jump for joy!"
                "RPG" -> "Epic quests await!"
                else -> "A unique gaming experience."
            }

            formattedList.add(
                "Date: $date\n" +
                        "Minutes: $minutes\n" +
                        "Genre: $genre\n" +
                        "Rating: $rating/5 ($feedback)\n" +
                        "-----------------------------------"
            )
        }
        return formattedList
    }

    // Parcelable implementation methods
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringArray(dates as Array<String?>) // Cast to nullable array
        parcel.writeArray(minutesPlayed as Array<out Any?>) // Cast to nullable Any array
        parcel.writeStringArray(gameGenres as Array<String?>) // Cast to nullable array
        parcel.writeArray(ratings as Array<out Any?>) // Cast to nullable Any array
        parcel.writeInt(entryCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GameSessionProcessor> {
        override fun createFromParcel(parcel: Parcel): GameSessionProcessor {
            return GameSessionProcessor(parcel)
        }

        override fun newArray(size: Int): Array<GameSessionProcessor?> {
            return arrayOfNulls(size)
        }
    }
}


object GameProcessor {
}