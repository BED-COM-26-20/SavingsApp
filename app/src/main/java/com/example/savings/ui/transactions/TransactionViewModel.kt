package com.example.savings.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savings.data.FirebaseDataSource
import com.example.savings.data.models.Transaction
import com.example.savings.data.models.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TransactionViewModel(private val firebaseDataSource: FirebaseDataSource) : ViewModel() {

    fun getTransactionsForMember(groupId: String, memberId: String): Flow<List<Transaction>> {
        return firebaseDataSource.getTransactions(groupId, memberId)
    }

    fun getAllTransactionsForGroup(groupId: String): Flow<List<Transaction>> {
        return firebaseDataSource.getMembers(groupId).flatMapLatest { members ->
            firebaseDataSource.getTransactions(groupId, members.first().id)
        }
    }

    fun addTransaction(
        memberId: String,
        amount: Double,
        type: TransactionType,
        date: Long,
        description: String,
        groupId: String
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                memberId = memberId,
                amount = amount,
                type = type,
                date = date,
                description = description,
                groupId = groupId
            )
            firebaseDataSource.addTransaction(groupId, memberId, transaction)
        }
    }
}
