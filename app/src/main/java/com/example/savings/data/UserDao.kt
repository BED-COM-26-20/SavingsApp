package com.example.savings.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.savings.data.models.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?
}
