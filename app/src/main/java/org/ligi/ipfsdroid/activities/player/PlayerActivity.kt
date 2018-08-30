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
import io.ipfs.kotlin.IPFS
import kotlinx.coroutines.experimental.async
import org.ligi.ipfsdroid.App
import java.io.File
import java.io.InputStream
import javax.inject.Inject


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

        // This is the MediaSource representing the media to be played.

        async {
            ipfs.get.catStream(contentHash) {
                Log.d(TAG, "Loading content as a stream $contentHash")
                val downloadFile = getDownloadFile(contentDescription)
                downloadFile.copyInputStreamToFile(it)
                Log.d(TAG, "Content downloaded")

                val myUri: Uri = Uri.fromFile(downloadFile)
                val mediaPlayer: MediaPlayer? = MediaPlayer().apply {
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    setDataSource(applicationContext, myUri)
                    prepare()
                    start()
                    Log.d(TAG, "Audio Started")
                }

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
}
