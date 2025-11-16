package com.example.savings.data.daos

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
    fun getMembersForGroup(groupId: String): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE id = :memberId")
    fun getMemberById(memberId: String): Flow<Member?>

    @Query("SELECT COUNT(*) FROM members WHERE groupId = :groupId")
    fun getMemberCountForGroup(groupId: String): Flow<Int>

    @Delete
    suspend fun delete(member: Member)

    @Query("DELETE FROM members")
    suspend fun clear()
}
