package org.ligi.ipfsdroid.repository

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import org.ligi.ipfsdroid.model.Feed

/**
 * Created by WillowTree on 9/6/18.
 */
@Dao
interface PlaylistDao {

    @Query("SELECT * from playlist WHERE hash == :selectedHash")
    fun getPlaylistItemByHash(selectedHash: String) : PlaylistItem

    @Query("SELECT * from playlist ORDER BY `index`")
    fun getAll(): List<PlaylistItem>

    @Query("SELECT * from playlist ORDER BY `index`")
    fun getAllLiveData(): LiveData<List<PlaylistItem>>

    @Insert(onConflict = REPLACE)
    fun insert(playlistItem: PlaylistItem)

    @Query("DELETE from playlist")
    fun deleteAll()

    @Query("DELETE from playlist WHERE hash == :selectedHash")
    fun deleteByHash(selectedHash: String)

    @Update(onConflict = REPLACE)
    fun updatePlaylistItem(playlistItem: PlaylistItem)

    @Transaction
    fun movePlayListItemByHash(hash: String, targetIndex: Int) {
        val playlistItem = getPlaylistItemByHash(hash)
        playlistItem.index = getDestinationIndex(getAll(), targetIndex)
        updatePlaylistItem(playlistItem)
    }


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
        val destinationIndex = getDestinationIndex(getAll(), targetIndex)
        playlistItem.index = destinationIndex
        if(playlistItem.index >= 0) {
            insert(playlistItem)
        }
    }

    @Transaction
    fun insertNewPlaylistItem(filename: String, feedItem: Feed) {
        val playlistItem = PlaylistItem(id = null,
                fileName = filename,
                hash = feedItem.file,
                bookmark = 0L,
                index = 0.0,
                name = feedItem.name,
                description = feedItem.description)
        insertPlayListItem(playlistItem, -1)
    }
}