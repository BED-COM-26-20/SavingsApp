package com.example.savings

import com.example.savings.data.FirebaseDataSource
import com.example.savings.data.daos.GroupDao
import com.example.savings.data.models.Group
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class CachingGroupRepository(private val groupDao: GroupDao, private val firebaseDataSource: FirebaseDataSource) {

    fun getGroups(): Flow<List<Group>> {
        return firebaseDataSource.getGroups().onEach { groups ->
            groupDao.deleteAll()
            groupDao.insertAll(*groups.toTypedArray())
        }
    }

    suspend fun createGroup(group: Group) {
        firebaseDataSource.createGroup(group)
    }

    suspend fun updateGroup(group: Group) {
        // TODO: Implement update in Firebase
        groupDao.update(group)
    }
}