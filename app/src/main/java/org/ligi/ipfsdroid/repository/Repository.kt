package org.ligi.ipfsdroid.repository

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.ipfs.kotlin.IPFS
import io.ipfs.kotlin.commands.Get
import io.ipfs.kotlin.model.BandWidthInfo
import io.ipfs.kotlin.model.VersionInfo
import org.ligi.ipfsdroid.model.BroadCastersList
import org.ligi.ipfsdroid.model.FeedsList
import java.io.InputStream

/**
 * Created by WillowTree on 8/31/18.
 */
class Repository(val ipfs: IPFS) {

    val TAG = Repository::class.simpleName

    val GLOBAL_FEEDS_HASH = "QmQyiSTZ2LFzBgVSMVP1tpCcugudfMfTMwb7VBZrUJmbTq"

    val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    fun getIpfsVersion(): VersionInfo? {
         return ipfs.info.version()
    }

    fun getBandWidth(): BandWidthInfo? {
        return ipfs.stats.bandWidth()
    }

    fun getStringByHash(hash: String): String {
        return ipfs.get.cat(hash)
    }

    fun getInputStreamFromHash(hash: String, handler: (InputStream) -> Unit) {
        ipfs.get.catStream(hash, handler)
        ipfs.pins.add(hash)
    }

    /**
     * This function doesn't actually work for some reason, it seems like it would be a cleaner way
     * to copy the file and a good use of extension functions, but it is not working for whatever reason.
     * Do no use.
     */
    fun getFile(hash: String, filename: String) {
        ipfs.get.getFile(hash, filename)
        ipfs.pins.add(hash)
    }

    fun getBroadCasters(): BroadCastersList? {
        // let's assume that we're getting a list of broadcasters from the web someplace and for now
        // just load it from the global feed hash
        val feeds = getStringByHash(GLOBAL_FEEDS_HASH)

        Log.d(TAG, "Got text from Feed Hash: $feeds")

        val jsonAdapter = moshi.adapter(BroadCastersList::class.java)
        return jsonAdapter.fromJson(feeds)
    }

    fun getFeedForBroadcaster(feedHash: String): FeedsList? {
        val jsonString = getStringByHash(feedHash)
        Log.d(TAG, jsonString)

        val feedsAdapter = moshi.adapter(FeedsList::class.java)
        return feedsAdapter.fromJson(jsonString)
    }

//    fun getPins(): Pins {
//        // TODO can I actually do this?
//
//    }

    private fun Get.getFile(hash: String, filename: String) {
        this.ipfs.callCmd("get/$hash -o $filename")
    }
}