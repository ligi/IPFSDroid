package org.ligi.ipfsdroid.repository

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction

/**
 * Created by WillowTree on 9/6/18.
 */
@Dao
interface PlaylistDao {

    @Query( "SELECT * from playlist ORDER BY `index`")
    fun getAll(): List<PlaylistItem>

    @Query( "SELECT * from playlist ORDER BY `index`")
    fun getAllLiveData(): LiveData<List<PlaylistItem>>

    @Insert(onConflict = REPLACE)
    fun insert(playlistItem: PlaylistItem)

    @Query("DELETE from playlist")
    fun deleteAll()

    @Query("DELETE from playlist WHERE hash == :selectedHash")
    fun deleteByHash(selectedHash: String)

    /**
     * Insert an item into the playlist at the target location
     * targetIndex = -1 means the intention is to add the item at the end of the list
     * targetIndex = 0 means the beginning of the list
     * targetIndex = else means the intention is to insert the item into the list at that index
     * A floating point number is used as an index to insert the items into the database without
     * needing to update the entire table
     */
    @Transaction
    fun insertPlayListItem(playlistItem: PlaylistItem, targetIndex: Int) {

        val allItems = getAll()
        if(targetIndex < allItems.size) {
            when (targetIndex) {
                -1 -> playlistItem.index = allItems.size.toDouble() + 1
                0 -> playlistItem.index = (0 + allItems[0].index) / 2
                else -> playlistItem.index = (allItems[targetIndex - 1].index + allItems[targetIndex + 1].index) / 2
            }
            insert(playlistItem)
        }
    }

    @Transaction
    fun insertNewPlaylistItem(filename: String, hash: String) {
        val allItems = getAll()
        val playlistItem = PlaylistItem(id = null, fileName = filename, hash = hash, bookmark = 0L, index = 0.0)
        insertPlayListItem(playlistItem, -1)
    }
}