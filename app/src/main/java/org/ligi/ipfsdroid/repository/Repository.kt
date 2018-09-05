package org.ligi.ipfsdroid.repository

import android.content.Context
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.ipfs.kotlin.IPFS
import io.ipfs.kotlin.model.NamedHash
import io.ipfs.kotlin.model.VersionInfo
import kotlinx.coroutines.experimental.async
import org.ligi.ipfsdroid.getFile
import org.ligi.ipfsdroid.getPins
import org.ligi.ipfsdroid.model.BroadCastersList
import org.ligi.ipfsdroid.model.FeedsList
import org.ligi.ipfsdroid.unPin
import java.io.File
import java.io.InputStream

/**
 * Created by WillowTree on 8/31/18.
 */
class Repository(val ipfs: IPFS) {


    companion object {
        const val DOWNLOADS_DIR_NAME = "downloads"
        const val GLOBAL_FEEDS_HASH = "QmQyiSTZ2LFzBgVSMVP1tpCcugudfMfTMwb7VBZrUJmbTq"
    }

    val TAG = Repository::class.simpleName

    val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

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
        Log.d(TAG, jsonString)

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

    /**
     * Add a file on local storage to IPFS, functionally, this is done just to ensure that the file
     * is pinned and not garbage collected from local storage until it is deleted by the user.
     * Whether or not this is necessary is an open question.
     */
    fun addFileToIPFS(file: File): NamedHash {
        return ipfs.add.file(file)
    }

    /**
     * This method unPins a file from IPFS.  Whether this pinning and unpinning is actually necessary
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
     * Get a file in the downloads directory of private storage for downloading the file for playback
     */
    fun getDownloadFile(description: String, context: Context): File {
        return File(context.getDir(DOWNLOADS_DIR_NAME, Context.MODE_PRIVATE), description)
    }

    /**
     * This function doesn't actually work for some reason, it seems like it would be a cleaner way
     * to copy the file and a good use of extension functions, but it is not working for whatever reason.
     * Do no use.
     */
    fun getFile(file: File, hash: String, filename: String) {
        val result = ipfs.get.getFile(hash, file.absolutePath)
        Log.d(TAG, "Result from getting file $result")
    }

}