package com.example.savings.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingDao {
    @Insert
    suspend fun insert(saving: Saving)

    @Query("SELECT * FROM savings ORDER BY date DESC")
    fun getAllSavings(): Flow<List<Saving>>
}
