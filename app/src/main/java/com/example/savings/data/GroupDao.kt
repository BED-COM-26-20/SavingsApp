package com.example.savings.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.savings.data.models.Group
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Insert
    suspend fun insertAll(vararg groups: Group)

    @Update
    suspend fun update(group: Group)

    @Query("DELETE FROM groups")
    suspend fun deleteAll()

    @Query("SELECT * FROM groups ORDER BY name ASC")
    fun getAllGroups(): Flow<List<Group>>
}
