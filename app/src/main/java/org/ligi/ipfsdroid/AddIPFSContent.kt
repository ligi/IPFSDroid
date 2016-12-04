package org.ligi.ipfsdroid

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import io.ipfs.kotlin.IPFS
import okio.Okio
import org.ligi.kaxt.loadImage
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File

@RuntimePermissions
class AddIPFSContent : HashTextAndBarcodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Intent.ACTION_SEND == intent.action) {
            if (intent.type != null && "text/plain" == intent.type) {
                handleSendText(intent) // Handle text being sent
            } else {
                AddIPFSContentPermissionsDispatcher.handleSendStreamWithCheck(this, intent)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AddIPFSContentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }

    internal fun handleSendText(intent: Intent) {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText != null) {
            addWithUI { IPFS().add.string(sharedText) }
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun handleSendStream(intent: Intent) {
        var uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)

        if (uri == null) {
            uri = intent.data
        }

        val inputStreamWithSource = InputStreamProvider.fromURI(this, uri)

        var createTempFile = File.createTempFile("import", null, cacheDir)

        if (inputStreamWithSource != null) {
            val sink = Okio.buffer(Okio.sink(createTempFile))

            val buffer = Okio.source(inputStreamWithSource.inputStream)
            sink.writeAll(buffer)
            sink.close()
        }

        if (inputStreamWithSource == null || !createTempFile.exists()) {
            createTempFile = uri.loadImage(this)
        }

        addWithUI {
            ipfs.add.file(createTempFile)
        }
    }

    override fun getSuccessDisplayHTML(): String {
        return "added <a href='${getSuccessURL()}'>${getSuccessURL()}</a>"
    }

    override fun getSuccessURL(): String {
        return "fs:/ipfs/${addResult!!.Hash}"
    }
}
