package org.ligi.ipfsdroid.activities.player


import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem

import android.widget.SeekBar
import android.widget.TextView
import org.ligi.ipfsdroid.R

import kotlinx.android.synthetic.main.activity_player.*
import kotlinx.android.synthetic.main.content_player.*
import kotlinx.coroutines.experimental.async
import org.ligi.ipfsdroid.App
import org.ligi.ipfsdroid.copyInputStreamToFile
import org.ligi.ipfsdroid.getReadableTimeFromMillis
import org.ligi.ipfsdroid.repository.Repository
import javax.inject.Inject

class PlayerActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener {

    // TODO right now everything that is clicked is implicitly downloaded, instead add functionality to download and add to playlist, then add a downloading indication for progress

    @Inject
    lateinit var repository: Repository

    lateinit var playerAdapter: PlayerAdapter

    var userIsSeeking: Boolean = false

    lateinit var durationTextView: TextView

    companion object {
        private val TAG = PlayerActivity::class.simpleName
        val EXTRA_CONTENT_HASH = "content_hash"
        val EXTRA_CONTENT_DESC = "content_description"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component().inject(this)
        setContentView(R.layout.activity_player)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val contentHash = intent.getStringExtra(EXTRA_CONTENT_HASH)
        val contentDescription = intent.getStringExtra(EXTRA_CONTENT_DESC)
        title = contentDescription

        playerAdapter = MediaPlayerHolder(this)
        playerAdapter.setPlaybackInfoListener(MyPlaybackInfoListener())
        initializeSeekbar()

        async {
            repository.getInputStreamFromHash(contentHash) {
                Log.d(TAG, "Loading content as a stream $contentHash")
                val downloadFile = repository.getDownloadFile(contentDescription, this@PlayerActivity)
                downloadFile.copyInputStreamToFile(it)
                Log.d(TAG, "Content downloaded")

                val myUri: Uri = Uri.fromFile(downloadFile)
                playerAdapter.loadMedia(myUri, this@PlayerActivity)
                val namedHash = repository.addFileToIPFS(downloadFile)
                Log.d(TAG, "Added content to IPFS: $namedHash")
            }
        }

        play_button.setOnClickListener {
            playerAdapter.play()
        }

        stop_button.setOnClickListener {
            onBackPressed()
        }

        pause_button.setOnClickListener {
            playerAdapter.pause()
        }

        durationTextView = findViewById(R.id.textViewDuration)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.d(TAG, "The end of the media has been reached")
        onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun initializeSeekbar() {
        seek_bar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {

            var userSelectedPosition: Int = 0

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    userSelectedPosition = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                userIsSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                userIsSeeking = false
                playerAdapter.seekTo(userSelectedPosition)
            }
        })
    }

    inner class MyPlaybackInfoListener : PlaybackInfoListener() {

        override fun onStateChanged(state: Int) {
            val stateString = PlaybackInfoListener.convertStateToString(state)
        }

        override fun onDurationChanged(duration: Int) {
            seek_bar.max = duration
            runOnUiThread {
                textViewDuration.text = getReadableTimeFromMillis(duration.toLong())
            }
        }

        override fun onPositionChanged(position: Int) {
            if(!userIsSeeking) {
                seek_bar.progress = position
                runOnUiThread {
                    textViewCurrentPosition.text = getReadableTimeFromMillis(position.toLong())
                }
            }
        }

        override fun onPlaybackCompleted() {
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        playerAdapter.release()
    }

}
