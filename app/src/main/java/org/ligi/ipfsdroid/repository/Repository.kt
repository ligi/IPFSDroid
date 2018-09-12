package org.ligi.ipfsdroid.repository

import android.arch.lifecycle.LiveData
import android.content.Context
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.ipfs.kotlin.IPFS
import io.ipfs.kotlin.model.NamedHash
import io.ipfs.kotlin.model.VersionInfo
import kotlinx.coroutines.experimental.async
import org.ligi.ipfsdroid.*
import org.ligi.ipfsdroid.model.BroadCastersList
import org.ligi.ipfsdroid.model.Feed
import org.ligi.ipfsdroid.model.FeedsList
import java.io.File
import java.io.InputStream
import javax.inject.Inject

/**
 * Created by WillowTree on 8/31/18.
 */
class Repository(val ipfs: IPFS) {

    companion object {
        const val DOWNLOADS_DIR_NAME = "downloads"
        const val GLOBAL_FEEDS_HASH = "QmcCmuBnd1o8NbZWWP7KESWhynCQmxkLS41q8Qk3xmoNYA"
    }

    val TAG = Repository::class.simpleName

    val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Inject
    lateinit var appContext: Context

    init {
        App.component().inject(this)
    }

    fun getIpfsVersion(): VersionInfo? {
        return ipfs.info.version()
    }

    private fun getStringByHash(hash: String): String {
        return ipfs.get.cat(hash)
    }

    fun getInputStreamFromHash(hash: String, handler: (InputStream) -> Unit) {
        ipfs.get.catStream(hash, handler)

    }

    fun getBroadCasters(): BroadCastersList? {
        // let's assume that we're getting a list of broadcasters from the web someplace and for now
        // just load it from the global feed hash
        val feeds = getStringByHash(GLOBAL_FEEDS_HASH)
        val jsonAdapter = moshi.adapter(BroadCastersList::class.java)
        return jsonAdapter.fromJson(feeds)
    }

    fun getFeedForBroadcaster(feedHash: String): FeedsList? {
        val jsonString = getStringByHash(feedHash)
        val feedsAdapter = moshi.adapter(FeedsList::class.java)
        return feedsAdapter.fromJson(jsonString)
    }

    fun getDownloadedFiles(context: Context): Array<out File>? {
        val downloadsDir = context.getDir(DOWNLOADS_DIR_NAME, Context.MODE_PRIVATE)
        return downloadsDir.listFiles()
    }

    fun deleteFile(file: File) {
        file.delete()
        unPinFile(file)
    }

    //region Playlist methods
    fun getPlaylist(): LiveData<List<PlaylistItem>>? {
        return PlaylistDatabase.getInstance(appContext)?.playListDao()?.getAllLiveData()
    }

    fun insertPlaylistItem(feedItem: Feed, downloadComplete: () -> Unit) {
        async {
            getInputStreamFromHash(feedItem.link) {
                Log.d(TAG, "Starting File Download")
                val downloadFile = getDownloadFile(feedItem.fileName, appContext)
                downloadFile.copyInputStreamToFile(it)

                addFileToIPFS(downloadFile)  // Ensures item is pinned recursively
                PlaylistDatabase.getInstance(appContext)?.playListDao()?.insertNewPlaylistItem(downloadFile.absolutePath, feedItem)
                downloadComplete.invoke()
                Log.d(TAG, "File Download Complete")
            }
        }
    }

    fun deletePlaylistItem(playlistItem: PlaylistItem) {
        async {
            PlaylistDatabase.getInstance(appContext)?.playListDao()?.deleteByHash(playlistItem.hash)
            deleteFile(File(playlistItem.fileName))
        }
    }

    fun movePlayListItem(hash: String, targetIndex: Int) {
        PlaylistDatabase.getInstance(appContext)?.playListDao()?.movePlayListItemByHash(hash, targetIndex)
    }

    fun updatePlaylistItem(playlistItem: PlaylistItem) {
        async {
            PlaylistDatabase.getInstance(appContext)?.playListDao()?.updatePlaylistItem(playlistItem)
        }
    }
    //endregion Playslist methods

    //region Compbination methods
    fun getFeedAndPlaylist(feedHash: String): Pair<FeedsList?, List<PlaylistItem>?> {
        Log.d(TAG, "Getting Feed and Playlist")
        val feedsList = getFeedForBroadcaster(feedHash)
        val playList = PlaylistDatabase.getInstance(appContext)?.playListDao()?.getAll()
        return Pair(feedsList, playList)
    }

    //endregion

    /**
     * Add a link on local storage to IPFS, functionally, this is done just to ensure that the link
     * is pinned and not garbage collected from local storage until it is deleted by the user.
     * Whether or not this is necessary is an open question.
     */
    fun addFileToIPFS(file: File): NamedHash {
        return ipfs.add.file(file)
    }

    /**
     * This method unPins a link from IPFS.  Whether this pinning and unpinning is actually necessary
     * is an open question
     */
    fun unPinFile(file: File) {
        async {
            val hash = addFileToIPFS(file).Hash
            ipfs.add.unPin(hash)
        }
    }

    fun getPins(): String {
        return ipfs.pins.getPins()
    }

    /**
     * Get a link in the downloads directory of private storage for downloading the link for playback
     */
    fun getDownloadFile(description: String, context: Context): File {
        return File(context.getDir(DOWNLOADS_DIR_NAME, Context.MODE_PRIVATE), description)
    }

    /**
     * This function doesn't actually work for some reason, it seems like it would be a cleaner way
     * to copy the link and a good use of extension functions, but it is not working for whatever reason.
     * Do no use.
     */
    fun getFile(file: File, hash: String, filename: String) {
        val result = ipfs.get.getFile(hash, file.absolutePath)
        Log.d(TAG, "Result from getting link $result")
    }
}