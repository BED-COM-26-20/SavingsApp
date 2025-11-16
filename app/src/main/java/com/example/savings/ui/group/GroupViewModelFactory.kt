package com.example.savings.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.savings.data.GroupRepository

class GroupViewModelFactory(private val groupRepository: GroupRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupViewModel(groupRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}