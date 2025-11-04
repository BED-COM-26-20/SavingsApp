package com.example.savings.ui.savings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savings.data.Saving
import com.example.savings.data.SavingDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavingsViewModel(private val savingDao: SavingDao) : ViewModel() {

    val savings: StateFlow<List<Saving>> = savingDao.getAllSavings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addSaving(amount: Double, description: String, date: Long) {
        viewModelScope.launch {
            savingDao.insert(Saving(amount = amount, description = description, date = date))
        }
    }
}
