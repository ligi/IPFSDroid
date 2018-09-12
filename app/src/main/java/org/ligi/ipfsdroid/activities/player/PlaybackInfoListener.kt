package org.ligi.ipfsdroid.activities.player

/**
 * Created by WillowTree on 8/30/18.
 */

abstract class PlaybackInfoListener {

    companion object {
        const val INVALID = -1
        const val PLAYING = 0
        const val PAUSED = 1
        const val RESET = 2
        const val COMPLETED = 3

        fun convertStateToString(state: Int): String {
            return when(state){
                INVALID -> "INVALID"
                PLAYING -> "PLAYING"
                PAUSED -> "PAUSED"
                RESET -> "RESET"
                COMPLETED -> "COMPLETED"
                else -> "N/A"
            }
        }
    }

    abstract fun onDurationChanged(duration: Int)

    abstract fun onPositionChanged(position: Int)

    abstract fun onStateChanged(state: Int)

    abstract fun onPlaybackCompleted()
}