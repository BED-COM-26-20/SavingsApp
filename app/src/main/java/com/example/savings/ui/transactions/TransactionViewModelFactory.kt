package com.example.savings.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.savings.data.FirebaseDataSource

class TransactionViewModelFactory(private val firebaseDataSource: FirebaseDataSource) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(firebaseDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
