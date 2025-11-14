package com.example.savings.data

import com.example.savings.data.models.UserRole

/**
 * A repository to handle all authentication tasks.
 */
class AuthRepository(private val firebaseDataSource: FirebaseDataSource) {

    // In a real app, you would get the current user's role from Firebase Custom Claims
    // or a 'users' collection in Firestore.
    val currentUserRole: UserRole = UserRole.ADMIN // Placeholder

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
