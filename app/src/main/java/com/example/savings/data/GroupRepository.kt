package com.example.savings.data

import com.example.savings.data.models.Group
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun getGroups(): Flow<List<Group>>
    suspend fun createGroup(groupName: String)
    suspend fun updateGroup(group: Group)
}
