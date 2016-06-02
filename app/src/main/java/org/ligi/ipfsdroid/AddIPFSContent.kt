package org.ligi.ipfsdroid

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import io.ipfs.kotlin.IPFS
import io.ipfs.kotlin.model.NamedHash
import net.steamcrafted.loadtoast.LoadToast
import org.ligi.axt.AXT
import org.ligi.tracedroid.logging.Log
import javax.inject.Inject

class AddIPFSContent : AppCompatActivity() {

    @Inject
    lateinit var ipfs:IPFS

    val hashText by lazy { findViewById(R.id.hash) as TextView }
    var addResult: NamedHash? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component().inject(this)
        setContentView(R.layout.activity_add)

        if (Intent.ACTION_SEND == intent.action && intent.type != null) {
            if ("text/plain" == intent.type) {
                handleSendText(intent) // Handle text being sent
            } else {
                handleSendStream(intent) // Handle single image being sent
            }
        }
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

                Snackbar.make(hashText, "copy " + addResult?.Hash, Snackbar.LENGTH_LONG).show()
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

    internal fun handleSendStream(intent: Intent) {
        val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        val file = AXT.at(imageUri).loadImage(this)

        addWithUI { ipfs.add.file(file) }
    }

    fun addWithUI(callback: () -> NamedHash) {

        val show = LoadToast(this).show()

        Thread(Runnable {
            addResult = callback()
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

                hashText.text = displayString

            }
        }).start()

    }

}
