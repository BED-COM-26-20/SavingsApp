package com.example.savings.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.savings.data.models.Group
import com.example.savings.data.models.Member
import com.example.savings.data.models.Transaction
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

    suspend fun getGroups(): List<Group> {
        val snapshot = db.collection("groups").get().await()
        return snapshot.toObjects(Group::class.java)
    }

    suspend fun createGroup(group: Group) {
        db.collection("groups").add(group).await()
    }

    // --- Members ---

    suspend fun getMembers(groupId: String): List<Member> {
        val snapshot = db.collection("groups").document(groupId).collection("members").get().await()
        return snapshot.toObjects(Member::class.java)
    }

    suspend fun addMember(groupId: String, member: Member) {
        db.collection("groups").document(groupId).collection("members").add(member).await()
    }

    // --- Transactions ---

    suspend fun getTransactions(groupId: String, memberId: String): List<Transaction> {
        val snapshot = db.collection("groups").document(groupId)
            .collection("members").document(memberId)
            .collection("transactions").get().await()
        return snapshot.toObjects(Transaction::class.java)
    }

    suspend fun addTransaction(groupId: String, memberId: String, transaction: Transaction) {
        db.collection("groups").document(groupId)
            .collection("members").document(memberId)
            .collection("transactions").add(transaction).await()
    }
}
