package com.example.savings.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings")
data class Saving(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val description: String,
    val date: Long = System.currentTimeMillis()
)
