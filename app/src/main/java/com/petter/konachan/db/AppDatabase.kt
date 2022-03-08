package com.petter.konachan.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.petter.konachan.response.Image
import com.petter.konachan.response.ImageEntity

/**
 * @anthor: EDZ
 * @time: 2021/11/9 13:47
 * @description:
 */
@Database(entities = [Image::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun imageDao(): ImageDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        private val sLock = Any()

        fun getInstance(context: Context): AppDatabase {
            synchronized(sLock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "h_ero.db"
                    ).allowMainThreadQueries()
//                        .addMigrations(object: Migration(1, 2){
//                            override fun migrate(database: SupportSQLiteDatabase) {
//
//                            }
//                        })
                        .build()
                }
                return INSTANCE!!
            }
        }
    }


}