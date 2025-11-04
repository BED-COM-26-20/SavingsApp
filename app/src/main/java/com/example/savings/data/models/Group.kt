package com.example.savings.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    // These will be calculated dynamically later
    val totalSavings: Double = 0.0,
    val totalLoans: Double = 0.0,
    val numberOfMembers: Int = 0
)
