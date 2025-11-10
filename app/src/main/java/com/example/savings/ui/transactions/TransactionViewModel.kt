package com.example.savings.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savings.data.TransactionDao
import com.example.savings.data.models.Transaction
import com.example.savings.data.models.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TransactionViewModel(private val transactionDao: TransactionDao) : ViewModel() {

    fun getTransactionsForMember(memberId: Int): Flow<List<Transaction>> {
        return transactionDao.getTransactionsForMember(memberId)
    }

    fun getAllTransactionsForGroup(groupId: Int): Flow<List<Transaction>> {
        return transactionDao.getAllTransactionsForGroup(groupId)
    }

    fun getGroupTotalSavings(groupId: Int): Flow<Double?> {
        return transactionDao.getGroupTotal(groupId, TransactionType.DEPOSIT)
    }

    fun getGroupTotalLoans(groupId: Int): Flow<Double?> {
        return transactionDao.getGroupTotal(groupId, TransactionType.LOAN)
    }

    fun getMemberTotalSavings(memberId: Int): Flow<Double?> {
        return transactionDao.getMemberTotal(memberId, TransactionType.DEPOSIT)
    }

    fun getMemberTotalLoans(memberId: Int): Flow<Double?> {
        return transactionDao.getMemberTotal(memberId, TransactionType.LOAN)
    }

    fun addTransaction(
        memberId: Int,
        amount: Double,
        type: TransactionType,
        date: Long,
        description: String
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                memberId = memberId,
                amount = amount,
                type = type,
                date = date,
                description = description
            )
            transactionDao.insert(transaction)
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            transactionDao.clear()
        }
    }
}
