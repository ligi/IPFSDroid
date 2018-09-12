package org.ligi.ipfsdroid.repository

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Created by WillowTree on 9/6/18.
 */
@Database(entities = [PlaylistItem::class], version = 1)
abstract class PlaylistDatabase : RoomDatabase() {

    abstract fun playListDao() : PlaylistDao

    companion object {

        private var INSTANCE: PlaylistDatabase? = null

        fun getInstance(context: Context): PlaylistDatabase? {
            if (INSTANCE == null) {
                synchronized(PlaylistDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            PlaylistDatabase::class.java, "playlist.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}