package com.example.savings.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.savings.data.models.Member
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Insert
    suspend fun insert(member: Member)

    @Query("SELECT * FROM members WHERE groupId = :groupId ORDER BY name ASC")
    fun getMembersForGroup(groupId: Int): Flow<List<Member>>

    @Delete
    suspend fun delete(member: Member)
}
