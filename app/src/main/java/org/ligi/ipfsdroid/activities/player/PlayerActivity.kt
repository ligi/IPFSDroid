package org.ligi.ipfsdroid.activities.player


import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

import android.widget.SeekBar
import org.ligi.ipfsdroid.R

import kotlinx.android.synthetic.main.activity_player.*
import io.ipfs.kotlin.IPFS
import kotlinx.android.synthetic.main.content_player.*
import kotlinx.coroutines.experimental.async
import org.ligi.ipfsdroid.App
import java.io.File
import java.io.InputStream
import javax.inject.Inject


class PlayerActivity : AppCompatActivity() {

    @Inject
    lateinit var ipfs: IPFS

    lateinit var playerAdapter: PlayerAdapter

    var userIsSeeking: Boolean = false

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

        val contentHash = intent.getStringExtra(EXTRA_CONTENT_HASH)
        val contentDescription = intent.getStringExtra(EXTRA_CONTENT_DESC)

        playerAdapter = MediaPlayerHolder(this)
        playerAdapter.setPlaybackInfoListener(MyPlaybackInfoListener())
        initializeSeekbar()

        // This is the MediaSource representing the media to be played.

        async {
            ipfs.get.catStream(contentHash) {
                Log.d(TAG, "Loading content as a stream $contentHash")
                val downloadFile = getDownloadFile(contentDescription)
                downloadFile.copyInputStreamToFile(it)
                Log.d(TAG, "Content downloaded")

                // TODO downloading the file feels like a hack, perhaps I can use the InputStream or byteArray as a data source

                val myUri: Uri = Uri.fromFile(downloadFile)
                playerAdapter.loadMedia(myUri)

//                mediaPlayer = MediaPlayer().apply {
//                    setAudioStreamType(AudioManager.STREAM_MUSIC)
//                    setDataSource(applicationContext, myUri)
//                    prepare()
//                    start()
//                    Log.d(TAG, "Audio Started")
//                }

            }
        }

        play_button.setOnClickListener {
            playerAdapter.play()
        }

        stop_button.setOnClickListener {
            // TODO player adapter has no such method
        }

        pause_button.setOnClickListener {
            playerAdapter.pause()
        }

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
        }

        override fun onPositionChanged(position: Int) {
            if(!userIsSeeking) {
                seek_bar.progress = position
            }
        }

        override fun onPlaybackCompleted() {
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        playerAdapter.release()
    }

    // TODO these should really be in their own directory
    private fun getDownloadFile(description: String): File {
        return File(this.filesDir, description)
    }

    fun File.copyInputStreamToFile(inputStream: InputStream) {
        inputStream.use { input ->
            this.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }
}
