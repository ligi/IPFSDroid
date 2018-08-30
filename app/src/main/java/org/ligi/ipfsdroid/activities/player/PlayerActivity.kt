package org.ligi.ipfsdroid.activities.player

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.exoplayer2.*
import org.ligi.ipfsdroid.R

import kotlinx.android.synthetic.main.activity_player.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import kotlinx.android.synthetic.main.content_player.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.*
import io.ipfs.kotlin.IPFS
import kotlinx.coroutines.experimental.async
import org.ligi.ipfsdroid.App
import java.io.File
import java.io.InputStream
import javax.inject.Inject
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.FileDataSource.FileDataSourceException
import com.google.android.exoplayer2.upstream.DataSpec






class PlayerActivity : AppCompatActivity() {

    @Inject
    lateinit var ipfs: IPFS

    lateinit var player: SimpleExoPlayer

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

        // 1. Create a default TrackSelector
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val dataSourceFactory = DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)), DefaultBandwidthMeter())

        // 2. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)


        // TODO test big buck bunny to rule out codec issues as the limiting factor
        // Disable video
        for (i in 0 until player.getRendererCount()) {
            if (player.getRendererType(i) == C.TRACK_TYPE_VIDEO) {
                trackSelector.setRendererDisabled(i, true)
            }
        }

        playerView.player = player

        player.addListener(eventListener)

        // This is the MediaSource representing the media to be played.

        async {
            ipfs.get.catStream(contentHash) {
                Log.d(TAG, "Loading content as a stream $contentHash")
                val downloadFile = getDownloadFile(contentDescription)
                downloadFile.copyInputStreamToFile(it)
                Log.d(TAG, "Content downloaded")

                // TODO experiment with media player
                val myUri: Uri = Uri.fromFile(downloadFile)
                val mediaPlayer: MediaPlayer? = MediaPlayer().apply {
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    setDataSource(applicationContext, myUri)
                    prepare()
                    start()
                    Log.d(TAG, "Audio Started")
                }

                // TODO maybe the easy way is to download the content to a file and read that in to the player - doesn't this necessarily duplicate storage required?
//                val videoUri = Uri.fromFile(downloadFile)
//                val createdUri = Uri.parse("asset://${downloadFile.absoluteFile}")
//                val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
//                        .createMediaSource(videoUri)
//
//
//                player.prepare(mediaSource)
//                Log.d(TAG, "MediaSource prepared")

                //                val dataSpec = DataSpec(Uri.fromFile(downloadFile))
//                val fileDataSource = FileDataSource()
//                try {
//                    fileDataSource.open(dataSpec)
//                } catch (e: FileDataSource.FileDataSourceException) {
//                    e.printStackTrace()
//                }
//
//
//                val factory = object : DataSource.Factory {
//                    override fun createDataSource(): DataSource {
//                        return fileDataSource
//                    }
//                }
//                val audioSource = ExtractorMediaSource(fileDataSource.uri,
//                        factory, DefaultExtractorsFactory(), null, null)



                // Obsolete stuff from trying unsuccessfully to play the file from a byteArray

//                val targetArray = ByteArray(it.available())
//                it.read(targetArray)

//                val byteArrayDataSource: ByteArrayDataSource = ByteArrayDataSource(targetArray)
//
//                val audioByteUri = UriByteDataHelper().getUri(targetArray)
//
//                val dataSpec = DataSpec(audioByteUri)
//                try {
//                    byteArrayDataSource.open(dataSpec)
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//
//                val factory = object : DataSource.Factory {
//                    override fun createDataSource(): DataSource {
//                        return byteArrayDataSource
//                    }
//                }
//                Log.i(Companion.TAG, "DataSource.Factory constructed.")
//
//                val videoSource = ExtractorMediaSource.Factory(factory)
//                        .createMediaSource(audioByteUri)
//                player.prepare(videoSource)
//                player.playWhenReady = true

            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
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


    private val eventListener = object : Player.EventListener {

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        }

        override fun onSeekProcessed() {
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            Log.d(TAG, "onPlayerError ${error.toString()}")
        }

        override fun onLoadingChanged(isLoading: Boolean) {
        }

        override fun onPositionDiscontinuity(reason: Int) {
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            Log.d(TAG, "onPlayerStateChanged playWhenReady = $playWhenReady  playbackState = $playbackState")
        }
    }
}
