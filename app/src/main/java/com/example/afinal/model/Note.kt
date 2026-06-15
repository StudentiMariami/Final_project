package com.example.afinal.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * MODEL LAYER (the "M" in MVVM)
 *
 * This data class represents a single Note in the app.
 * It is also a Room DATABASE TABLE thanks to the @Entity annotation.
 *
 * @Entity(tableName = "notes") — tells Room to create a table called "notes"
 * @PrimaryKey(autoGenerate = true) — Room auto-increments the ID for each new row
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,           // Unique identifier, auto-assigned by Room

    val title: String,         // Title of the note (shown in the list)
    val content: String,       // Full text content of the note
    val imageUrl: String = "", // URL of an optional image (loaded with Glide — NEW FEATURE)
    val timestamp: Long = System.currentTimeMillis() // When the note was created
)
