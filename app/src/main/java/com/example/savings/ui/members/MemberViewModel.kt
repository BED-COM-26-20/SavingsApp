package com.example.savings.ui.members

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savings.data.MemberDao
import com.example.savings.data.models.Member
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MemberViewModel(private val memberDao: MemberDao) : ViewModel() {

    fun getMembersForGroup(groupId: Int): Flow<List<Member>> {
        return memberDao.getMembersForGroup(groupId)
    }

    fun addMember(name: String, phone: String, groupId: Int) {
        viewModelScope.launch {
            memberDao.insert(Member(name = name, phone = phone, groupId = groupId))
        }
    }

    fun deleteMember(member: Member) {
        viewModelScope.launch {
            memberDao.delete(member)
        }
    }
}
