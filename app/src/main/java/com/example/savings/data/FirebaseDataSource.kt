package com.example.savings.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.example.savings.data.models.Group
import com.example.savings.data.models.Member
import com.example.savings.data.models.Transaction
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseDataSource {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // --- Authentication ---

    suspend fun signIn(email: String, password: String): String {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw Exception("Sign in failed")
    }

    suspend fun register(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw Exception("Registration failed")
    }

    fun signOut() {
        auth.signOut()
    }

    // --- Groups ---

    fun getGroups(): Flow<List<Group>> = callbackFlow {
        val listener = db.collection("groups").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            snapshot?.let {
                val groups = it.toObjects<Group>()
                trySend(groups)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun createGroup(group: Group) {
        db.collection("groups").add(group).await()
    }

    suspend fun updateGroup(group: Group) {
        db.collection("groups").document(group.id).set(group).await()
    }

    // --- Members ---

    fun getMembers(groupId: String): Flow<List<Member>> = callbackFlow {
        val listener = db.collection("groups").document(groupId).collection("members")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val members = it.toObjects<Member>()
                    trySend(members)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun addMember(groupId: String, member: Member) {
        db.collection("groups").document(groupId).collection("members").add(member).await()
    }

    // --- Transactions ---

    fun getTransactions(groupId: String, memberId: String): Flow<List<Transaction>> = callbackFlow {
        val listener = db.collection("groups").document(groupId).collection("members")
            .document(memberId).collection("transactions").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val transactions = it.toObjects<Transaction>()
                    trySend(transactions)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun addTransaction(groupId: String, memberId: String, transaction: Transaction) {
        db.collection("groups").document(groupId).collection("members")
            .document(memberId).collection("transactions").add(transaction).await()
    }
}
