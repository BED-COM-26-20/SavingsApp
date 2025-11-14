package com.example.savings.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savings.data.GroupRepository
import com.example.savings.data.models.Group
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupViewModel(private val groupRepository: GroupRepository) : ViewModel() {

    val groups: StateFlow<List<Group>> = groupRepository.getAllGroupsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createGroup(groupName: String) {
        viewModelScope.launch {
            groupRepository.insertGroup(Group(name = groupName))
        }
    }

    fun updateGroup(group: Group) {
        viewModelScope.launch {
            groupRepository.updateGroup(group)
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            groupRepository.clearGroups()
        }
    }
}
