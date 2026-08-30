package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ArticleItem
import com.example.data.model.CalcHistoryEntity
import com.example.data.model.ContactMessageEntity
import com.example.data.model.DrugItem

@Database(
    entities = [
        DrugItem::class,
        CalcHistoryEntity::class,
        ArticleItem::class,
        ContactMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PharmacyDatabase : RoomDatabase() {
    abstract fun pharmacyDao(): PharmacyDao

    companion object {
        @Volatile
        private var INSTANCE: PharmacyDatabase? = null

        fun getDatabase(context: Context): PharmacyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PharmacyDatabase::class.java,
                    "pharmacist_mikiyas_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
