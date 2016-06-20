package org.ligi.ipfsdroid

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.ipfs.kotlin.IPFS
import io.ipfs.kotlin.model.NamedHash
import kotlinx.android.synthetic.main.activity_add.*
import net.steamcrafted.loadtoast.LoadToast
import org.ligi.axt.AXT
import org.ligi.tracedroid.logging.Log
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.net.ConnectException
import javax.inject.Inject

@RuntimePermissions
class AddIPFSContent : AppCompatActivity() {

    @Inject
    lateinit var ipfs: IPFS

    var addResult: NamedHash? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component().inject(this)
        setContentView(R.layout.activity_add)

        if (Intent.ACTION_SEND == intent.action && intent.type != null) {
            if ("text/plain" == intent.type) {
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.copy -> {
                val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager;
                val clip = ClipData.newPlainText("hash", addResult?.Hash);
                clipboardManager.primaryClip = clip;

                Snackbar.make(hashEditText, "copy " + addResult?.Hash, Snackbar.LENGTH_LONG).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    internal fun handleSendText(intent: Intent) {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText != null) {
            addWithUI { IPFS().add.string(sharedText) }
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun handleSendStream(intent: Intent) {
        val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        val file = AXT.at(imageUri).loadImage(this)

        addWithUI {
            ipfs.add.file(file)
        }
    }

    fun addWithUI(callback: () -> NamedHash) {

        val show = LoadToast(this).show()

        Thread(Runnable {
            try {
                addResult = callback()
            } catch (e: ConnectException) {
                addResult = null
            }
            runOnUiThread {
                val displayString: String
                if (addResult == null) {
                    show.error()
                    displayString = "could not execute add ( daemon running? )"
                } else {
                    show.success()
                    displayString = "added /ipfs/" + addResult!!.Hash
                }

                Log.i(displayString)

                hashEditText.text = displayString

            }
        }).start()

    }

}
