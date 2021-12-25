package com.rahul.notetaking.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EncryptFileEntity::class], version = 2, exportSchema = false)
abstract class EncryptFileDatabase: RoomDatabase() {
    abstract fun encFileDao(): EncryptFileDao


    companion object {

        const val DB_NAME = "enc_db"
        @Volatile
        private var INSTANCE: EncryptFileDatabase? = null

        fun getDatabase(context: Context): EncryptFileDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EncryptFileDatabase::class.java,
                    DB_NAME
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}