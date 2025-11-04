package com.example.savings.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class TransactionType {
    DEPOSIT,
    LOAN,
    LOAN_REPAYMENT
}

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Member::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val memberId: Int,
    val amount: Double,
    val type: TransactionType,
    val date: Long,
    val description: String
)
