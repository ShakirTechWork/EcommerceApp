package com.example.shakirtestproject.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shakirtestproject.models.CartItemModel

@Database(entities = [CartItemModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun roomDao(): RoomDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(context,
                        AppDatabase::class.java,
                        "cartDB")
                        .build()
                }
            }
            return INSTANCE!!
        }

    }

}