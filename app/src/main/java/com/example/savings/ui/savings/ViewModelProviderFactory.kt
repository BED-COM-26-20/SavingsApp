package com.example.savings.ui.savings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.savings.data.SavingDao

class ViewModelProviderFactory(private val savingDao: SavingDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SavingsViewModel(savingDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
