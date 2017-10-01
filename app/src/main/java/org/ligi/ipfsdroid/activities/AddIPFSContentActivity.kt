package org.ligi.ipfsdroid.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import io.ipfs.kotlin.IPFS
import okio.Okio
import org.ligi.ipfsdroid.InputStreamProvider
import org.ligi.kaxt.loadImage
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File

@RuntimePermissions
class AddIPFSContentActivity : HashTextAndBarcodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Intent.ACTION_SEND == intent.action) {
            if (intent.type != null && "text/plain" == intent.type) {
                handleSendText(intent) // Handle text being sent
            } else {
                handleSendStreamWithPermissionCheck(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun handleSendText(intent: Intent) {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText != null) {
            addWithUI { IPFS().add.string(sharedText) }
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun handleSendStream(intent: Intent) {
        val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM) ?: intent.data

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

    override fun getSuccessDisplayHTML()
            = "added <a href='${getSuccessURL()}'>${getSuccessURL()}</a>"

    override fun getSuccessURL() = "fs:/ipfs/${addResult!!.Hash}"
}
