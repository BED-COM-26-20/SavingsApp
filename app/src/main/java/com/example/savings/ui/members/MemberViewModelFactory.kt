package com.example.savings.ui.members

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.savings.data.MemberDao

class MemberViewModelFactory(private val memberDao: MemberDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MemberViewModel(memberDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
