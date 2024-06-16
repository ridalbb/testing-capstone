package com.bangkit.capstoneproject.cleanrubbish.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HistoryResult::class], version = 1)
abstract class HistoryResultRoomDatabase : RoomDatabase() {
    abstract fun HistoryResultDao(): HistoryResultDao

    companion object {
        @Volatile
        private var INSTANCE: HistoryResultRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): HistoryResultRoomDatabase {
            if (INSTANCE == null){
                synchronized(HistoryResultRoomDatabase::class.java){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        HistoryResultRoomDatabase::class.java,
                        "History_Result_Database"
                    ).build()
                }
            }
            return INSTANCE as HistoryResultRoomDatabase
        }
    }

}