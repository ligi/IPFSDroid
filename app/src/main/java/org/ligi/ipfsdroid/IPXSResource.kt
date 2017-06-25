package org.ligi.ipfsdroid

import android.net.Uri
import android.text.TextUtils

class IPXSResource(uriToRewrite: Uri) {

    enum class IPXSType {
        IPFS,
        IPNS
    }

    val address: String by lazy {
        val path = uriToRewrite.path
        when (uriToRewrite.scheme) {
            "fs" -> {
                if (uriToRewrite.host != null) {
                    path.substring(1)
                } else {
                    TextUtils.join("/", uriToRewrite.pathSegments.subList(1, uriToRewrite.pathSegments.size))
                }
            }
            "ipfs", "ipns" -> uriToRewrite.schemeSpecificPart.substring(2)
            "http", "https" -> TextUtils.join("/", uriToRewrite.pathSegments.subList(1, uriToRewrite.pathSegments.size))
            else -> throw IllegalArgumentException("Could not resolve address for " + uriToRewrite.toString())
        }
    }

    val type: IPXSType by lazy {
        when (uriToRewrite.scheme) {
            "fs" -> when (extractTypeStringFromFSURI(uriToRewrite)) {
                "ipfs" -> IPXSType.IPFS
                "ipns" -> IPXSType.IPNS
                else -> throw IllegalArgumentException("When scheme is fs:// then it must follow with ipfs or ipns but was " + extractTypeStringFromFSURI(uriToRewrite))
            }
            "ipfs" -> IPXSType.IPFS
            "ipns" -> IPXSType.IPNS
            "http", "https" -> {
                if (uriToRewrite.host != "ipfs.io") {
                    throw IllegalArgumentException("when scheme is http(s) then host has to be ipfs.io")
                }
                when (uriToRewrite.pathSegments[0]) {
                    "ipfs" -> IPXSType.IPFS
                    "ipns" -> IPXSType.IPNS
                    else -> throw IllegalArgumentException("cannot handle this ipfs.io url " + uriToRewrite.pathSegments[0])
                }
            }
            else -> throw IllegalArgumentException("scheme not supported")
        }
    }

    private fun extractTypeStringFromFSURI(uriToRewrite: Uri) = if (uriToRewrite.host != null) {
        uriToRewrite.host.toLowerCase()
    } else {
        uriToRewrite.pathSegments[0].replace("/", "")
    }

    fun ipfsioAddress() = "https://ipfs.io/" + type.toString().toLowerCase() + "/" + address + "/"

    override fun toString() = type.toString().toLowerCase() + ":" + address
}
