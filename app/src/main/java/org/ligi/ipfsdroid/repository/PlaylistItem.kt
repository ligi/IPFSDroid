package org.ligi.ipfsdroid.repository

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

/**
 * Created by WillowTree on 9/6/18.
 */
@Entity(tableName = "playlist", indices = [Index(value = ["index"])])
data class PlaylistItem(

        @PrimaryKey(autoGenerate = true)
        var id: Long?,

        @ColumnInfo(name = "index")
        var index: Double,

        @ColumnInfo(name = "file_name")
        var fileName: String,

        @ColumnInfo(name = "hash")
        var hash: String,

        @ColumnInfo(name = "bookmark")
        var bookmark: Long,

        @ColumnInfo(name = "description")
        var description: String,

        @ColumnInfo(name = "name")
        var name: String)