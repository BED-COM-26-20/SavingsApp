package com.example.savings.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.savings.data.models.Group
import com.example.savings.data.models.Member
import com.example.savings.data.models.Transaction
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseDataSource {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

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
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val groups = snapshot.children.mapNotNull { ds ->
                    ds.getValue(Group::class.java)?.copy(id = ds.key)
                }
                trySend(groups)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        db.child("groups").addValueEventListener(listener)
        awaitClose { db.child("groups").removeEventListener(listener) }
    }

    suspend fun createGroup(group: Group) {
        val newGroupRef = db.child("groups").push()
        newGroupRef.setValue(group.copy(id = newGroupRef.key)).await()
    }

    // --- Members ---

    fun getMembers(groupId: String): Flow<List<Member>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val members = snapshot.children.mapNotNull { ds ->
                    ds.getValue(Member::class.java)?.copy(id = ds.key)
                }
                trySend(members)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        db.child("groups").child(groupId).child("members").addValueEventListener(listener)
        awaitClose { db.child("groups").child(groupId).child("members").removeEventListener(listener) }
    }


    suspend fun addMember(groupId: String, member: Member) {
        val newMemberRef = db.child("groups").child(groupId).child("members").push()
        newMemberRef.setValue(member.copy(id = newMemberRef.key)).await()
    }

    // --- Transactions ---

    fun getTransactions(groupId: String, memberId: String): Flow<List<Transaction>> = callbackFlow {
         val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactions = snapshot.children.mapNotNull { ds ->
                    ds.getValue(Transaction::class.java)?.copy(id = ds.key)
                }
                trySend(transactions)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        db.child("groups").child(groupId).child("members").child(memberId).child("transactions").addValueEventListener(listener)
        awaitClose {  db.child("groups").child(groupId).child("members").child(memberId).child("transactions").removeEventListener(listener) }
    }

    suspend fun addTransaction(groupId: String, memberId: String, transaction: Transaction) {
        val newTransactionRef = db.child("groups").child(groupId).child("members").child(memberId).child("transactions").push()
        newTransactionRef.setValue(transaction.copy(id = newTransactionRef.key)).await()
    }
}
