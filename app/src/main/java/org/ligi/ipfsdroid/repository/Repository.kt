package org.ligi.ipfsdroid.repository

import io.ipfs.kotlin.IPFS
import io.ipfs.kotlin.model.BandWidthInfo
import io.ipfs.kotlin.model.VersionInfo
import java.io.InputStream

/**
 * Created by WillowTree on 8/31/18.
 */
class Repository(val ipfs: IPFS) {

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
    }
}