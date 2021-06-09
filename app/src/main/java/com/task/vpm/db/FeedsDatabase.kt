package com.task.vpm.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Feeds::class], version = 1)
abstract class FeedsDatabase : RoomDatabase() {
    abstract val feedsDAO: FeedsDAO

    companion object {
        @Volatile
        private var INSTANCE: FeedsDatabase? = null
        fun getInstance(context: Context): FeedsDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        FeedsDatabase::class.java,
                        "feeds_data_database"
                    ).build()
                }
                return instance
            }
        }
    }
}

