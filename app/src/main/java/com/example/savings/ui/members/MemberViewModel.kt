package com.example.savings.ui.members

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savings.data.FirebaseDataSource
import com.example.savings.data.models.Member
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MemberViewModel(private val firebaseDataSource: FirebaseDataSource) : ViewModel() {

    fun getMembersForGroup(groupId: String): Flow<List<Member>> {
        return firebaseDataSource.getMembers(groupId)
    }

    fun getMemberById(groupId: String, memberId: String): Flow<Member?> {
        return firebaseDataSource.getMembers(groupId).map { members ->
            members.find { it.id == memberId }
        }
    }

    fun addMember(name: String, phone: String, groupId: String) {
        viewModelScope.launch {
            firebaseDataSource.addMember(groupId, Member(name = name, phone = phone, groupId = groupId))
        }
    }
}
