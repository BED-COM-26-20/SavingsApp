package com.example.savings.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.savings.data.models.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE memberId = :memberId ORDER BY date DESC")
    fun getTransactionsForMember(memberId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE memberId IN (SELECT id FROM members WHERE groupId = :groupId) ORDER BY date DESC")
    fun getAllTransactionsForGroup(groupId: Int): Flow<List<Transaction>>
}
