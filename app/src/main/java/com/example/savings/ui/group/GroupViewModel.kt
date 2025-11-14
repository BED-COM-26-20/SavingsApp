package com.example.savings.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savings.CachingGroupRepository
import com.example.savings.data.models.Group
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupViewModel(private val groupRepository: CachingGroupRepository) : ViewModel() {

    val groups: StateFlow<List<Group>> = groupRepository.getGroups()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createGroup(groupName: String) {
        viewModelScope.launch {
            groupRepository.createGroup(Group(name = groupName))
        }
    }

    fun updateGroup(group: Group) {
        viewModelScope.launch {
            groupRepository.updateGroup(group)
        }
    }
}
