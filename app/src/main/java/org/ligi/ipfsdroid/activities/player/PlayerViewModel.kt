package org.ligi.ipfsdroid.activities.player

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.ligi.ipfsdroid.repository.PlaylistItem
import org.ligi.ipfsdroid.repository.Repository

/**
 * Created by WillowTree on 9/11/18.
 */
class PlayerViewModel : ViewModel() {

    lateinit var repository: Repository


    fun getPlaylist() : LiveData<List<PlaylistItem>>? {
        return repository.getPlaylist()
    }
}