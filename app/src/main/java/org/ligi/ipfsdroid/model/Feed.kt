package org.ligi.ipfsdroid.model

import org.ligi.ipfsdroid.repository.PlaylistItem

/**
 * Created by WillowTree on 8/29/18.
 */
data class Feed(val name: String, val description: String, val file: String) {

    fun isInPlayList(playlist: List<PlaylistItem>) : Boolean {
        for (item in playlist) {
            if (item.hash == file) {
                return true
            }
        }
        return false
    }
}