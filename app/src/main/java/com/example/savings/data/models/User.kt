package com.example.savings.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    // In a real app, this should be a secure hash of the password, not the plain text.
    val password: String 
)
