package com.example.savings.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "members",
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Member(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val groupId: Int,
    val name: String,
    val phone: String,
    val totalSavings: Double = 0.0,
    val totalLoan: Double = 0.0
)
