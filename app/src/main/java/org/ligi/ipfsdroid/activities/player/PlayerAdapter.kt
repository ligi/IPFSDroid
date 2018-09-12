package org.ligi.ipfsdroid.activities.player

import android.media.MediaPlayer
import android.net.Uri

/**
 * Created by WillowTree on 8/30/18.
 * Borrowed heavily from the simple audio sample app
 */

interface PlayerAdapter {

    fun setPlaybackInfoListener(listener: PlaybackInfoListener)

    fun release()

    fun isplaying(): Boolean

    fun play()

    fun pause()

    fun initializeProgressCallback()

    fun seekTo(position: Int)

    fun loadMedia(uri: Uri, listener: MediaPlayer.OnCompletionListener, bookmark: Long)
}