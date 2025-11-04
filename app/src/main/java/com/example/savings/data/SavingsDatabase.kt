package com.example.savings.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.savings.data.models.Group
import com.example.savings.data.models.Member
import com.example.savings.data.models.Transaction

@Database(entities = [Member::class, Transaction::class, Group::class], version = 3, exportSchema = false)
abstract class SavingsDatabase : RoomDatabase() {

    abstract fun memberDao(): MemberDao
    abstract fun transactionDao(): TransactionDao
    abstract fun groupDao(): GroupDao

    companion object {
        @Volatile
        private var INSTANCE: SavingsDatabase? = null

        fun getDatabase(context: Context): SavingsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SavingsDatabase::class.java,
                    "savings_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
