package com.example.savings.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.savings.data.models.Transaction
import com.example.savings.data.models.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE memberId = :memberId ORDER BY date DESC")
    fun getTransactionsForMember(memberId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE memberId IN (SELECT id FROM members WHERE groupId = :groupId) ORDER BY date DESC")
    fun getAllTransactionsForGroup(groupId: Int): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE memberId IN (SELECT id FROM members WHERE groupId = :groupId) AND type = :type")
    fun getGroupTotal(groupId: Int, type: TransactionType): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE memberId = :memberId AND type = :type")
    fun getMemberTotal(memberId: Int, type: TransactionType): Flow<Double?>

    @Query("DELETE FROM transactions")
    suspend fun clear()
}
