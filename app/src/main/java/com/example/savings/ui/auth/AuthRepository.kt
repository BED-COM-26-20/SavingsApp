package com.example.savings.ui.auth

import com.example.savings.data.FirebaseDataSource

class AuthRepository(private val firebaseDataSource: FirebaseDataSource) {

    suspend fun signIn(email: String, password: String): String {
        return firebaseDataSource.signIn(email, password)
    }

    suspend fun register(email: String, password: String): String {
        return firebaseDataSource.register(email, password)
    }

    fun signOut() {
        firebaseDataSource.signOut()
    }
}
