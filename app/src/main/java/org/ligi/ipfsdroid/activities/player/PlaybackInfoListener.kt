package org.ligi.ipfsdroid.activities.player

import android.support.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by WillowTree on 8/30/18.
 */

abstract class PlaybackInfoListener {

    // TODO make this constants caps
    companion object {
        const val invalid = -1
        const val playing = 0
        const val paused = 1
        const val reset = 2
        const val completed = 3

        fun convertStateToString(state: Int): String {
            return when(state){
                invalid -> "INVALID"
                playing -> "PLAYING"
                paused -> "PAUSED"
                reset -> "RESET"
                completed -> "COMPLETED"
                else -> "N/A"
            }
        }
    }

    abstract fun onDurationChanged(duration: Int)

    abstract fun onPositionChanged(position: Int)

    abstract fun onStateChanged(state: Int)

    abstract fun onPlaybackCompleted()
}