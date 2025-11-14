package com.example.savings.data

import com.example.savings.data.models.Group
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Group] from a given data source.
 */
interface GroupRepository {
    /**
     * Retrieve all the groups from the given data source.
     */
    fun getAllGroupsStream(): Flow<List<Group>>

    /**
     * Insert group in the data source
     */
    suspend fun insertGroup(group: Group)

    /**
     * Update group in the data source
     */
    suspend fun updateGroup(group: Group)

    /**
     * Clear all groups from the data source
     */
    suspend fun clearGroups()
}

class CachingGroupRepository(
    private val groupDao: GroupDao,
    private val firebaseDataSource: FirebaseDataSource
) : GroupRepository {

    override fun getAllGroupsStream(): Flow<List<Group>> {
        // In a real app, you would add logic here to fetch from Firebase
        // and update the local Room database.
        return groupDao.getAllGroups()
    }

    override suspend fun insertGroup(group: Group) {
        groupDao.insert(group)
        // firebaseDataSource.createGroup(group) // Uncomment to save to Firebase
    }

    override suspend fun updateGroup(group: Group) {
        groupDao.update(group)
    }

    override suspend fun clearGroups() {
        groupDao.clear()
    }
}
