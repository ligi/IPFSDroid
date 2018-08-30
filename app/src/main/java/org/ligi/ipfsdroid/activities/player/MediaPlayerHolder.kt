package org.ligi.ipfsdroid.activities.player

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Created by WillowTree on 8/30/18.
 * Adapted heavily from the Sample App
 */

class MediaPlayerHolder(var context: Context) : PlayerAdapter {

    init {
        context = context.applicationContext  //Enforce application context
    }

    val PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000

    private var mediaPlayer: MediaPlayer? = null
    private var playbackInfoListener: PlaybackInfoListener? = null
    private var executor: ScheduledExecutorService? = null
    private var seekbarPositionUpdateTask: Runnable? = null


    //region PlayerAdapter Implementation

    override fun setPlaybackInfoListener(listener: PlaybackInfoListener) {
        playbackInfoListener = listener
    }

    override fun loadMedia(uri: Uri) {
        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(context, uri)
            prepare()
            start()
            setOnCompletionListener {
                // TODO implement
            }
        }
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun isplaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun play() {
        if(!isplaying()) {
            mediaPlayer?.start()
            playbackInfoListener?.onStateChanged(PlaybackInfoListener.PLAYING)
            startUpdatingCallbackWithPosition()
        }
    }

    private fun startUpdatingCallbackWithPosition() {
        if (executor == null) {
            executor = Executors.newSingleThreadScheduledExecutor()
        }
        if (seekbarPositionUpdateTask == null) {
            seekbarPositionUpdateTask = Runnable { updateProgressCallbackTask() }
        }
        executor?.scheduleAtFixedRate(
                seekbarPositionUpdateTask,
                0,
                PLAYBACK_POSITION_REFRESH_INTERVAL_MS.toLong(),
                TimeUnit.MILLISECONDS
        )
    }

    private fun updateProgressCallbackTask() {
        if(isplaying()) {
            val currentPosition: Int = mediaPlayer?.currentPosition ?: 0
            playbackInfoListener?.onPositionChanged(currentPosition)
        }
    }

    override fun pause() {
        if(isplaying()) {
            mediaPlayer?.pause()
            playbackInfoListener?.onStateChanged(PlaybackInfoListener.PAUSED)
        }
    }

    override fun initializeProgressCallback() {
        val duration: Int = mediaPlayer?.duration ?: 0
        playbackInfoListener?.onDurationChanged(duration)
        playbackInfoListener?.onPositionChanged(0)
    }

    override fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }
    //endregion


}