package com.example.savings.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savings.data.GroupDao
import com.example.savings.data.models.Group
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupViewModel(private val groupDao: GroupDao) : ViewModel() {

    val groups: StateFlow<List<Group>> = groupDao.getAllGroups()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createGroup(groupName: String) {
        viewModelScope.launch {
            groupDao.insert(Group(name = groupName))
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            groupDao.clear()
        }
    }
}
