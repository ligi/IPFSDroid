package org.ligi.ipfsdroid.activities.player

import android.net.Uri

/**
 * Created by WillowTree on 8/30/18.
 * Borrowed heavily from the simple audio sample app
 */

interface PlayerAdapter {

    fun setPlaybackInfoListener(listener: PlaybackInfoListener)

    fun loadMedia(uri: Uri)

    fun release()

    fun isplaying(): Boolean

    fun play()

    fun pause()

    fun initializeProgressCallback()

    fun seekTo(position: Int)
}