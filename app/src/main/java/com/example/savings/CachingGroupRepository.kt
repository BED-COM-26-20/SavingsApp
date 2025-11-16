package com.example.savings

import com.example.savings.data.FirebaseDataSource
import com.example.savings.data.GroupRepository
import com.example.savings.data.models.Group
import kotlinx.coroutines.flow.Flow

class GroupRepositoryImpl(private val firebaseDataSource: FirebaseDataSource) : GroupRepository {

    override fun getGroups(): Flow<List<Group>> {
        return firebaseDataSource.getGroups()
    }

    override suspend fun createGroup(groupName: String) {
        firebaseDataSource.createGroup(Group(name = groupName))
    }

    override suspend fun updateGroup(group: Group) {
        firebaseDataSource.updateGroup(group)
    }
}
