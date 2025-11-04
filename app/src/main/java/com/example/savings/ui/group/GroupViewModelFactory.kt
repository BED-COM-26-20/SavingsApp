package com.example.savings.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.savings.data.GroupDao

class GroupViewModelFactory(private val groupDao: GroupDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupViewModel(groupDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
