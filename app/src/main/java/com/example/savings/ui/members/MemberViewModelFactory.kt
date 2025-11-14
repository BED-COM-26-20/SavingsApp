package com.example.savings.ui.members

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.savings.data.FirebaseDataSource

class MemberViewModelFactory(private val firebaseDataSource: FirebaseDataSource) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MemberViewModel(firebaseDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
