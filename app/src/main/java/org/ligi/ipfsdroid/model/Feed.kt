package org.ligi.ipfsdroid.model

import org.ligi.ipfsdroid.repository.PlaylistItem

/**
 * Created by WillowTree on 8/29/18.
 */
data class Feed(val title: String, val description: String, val fileName: String, val link: String) {

    fun isInPlayList(playlist: List<PlaylistItem>) : Boolean {
        for (item in playlist) {
            if (item.hash == link) {
                return true
            }
        }
        return false
    }
}