package org.ligi.ipfsdroid

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import io.ipfs.kotlin.IPFS
import io.ipfs.kotlin.model.NamedHash
import kotlinx.android.synthetic.main.activity_add.*
import net.glxn.qrgen.android.QRCode
import net.steamcrafted.loadtoast.LoadToast
import org.ligi.tracedroid.logging.Log
import java.net.ConnectException
import javax.inject.Inject

abstract class HashTextAndBarcodeActivity : AppCompatActivity() {

    @Inject
    lateinit var ipfs: IPFS

    var addResult: NamedHash? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component().inject(this)
        setContentView(R.layout.activity_add)

        hashInfoText.movementMethod = LinkMovementMethod.getInstance()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.copy -> {
                val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager;
                val clip = ClipData.newPlainText("hash", addResult?.Hash)
                clipboardManager.primaryClip = clip

                Snackbar.make(hashInfoText, "copy " + addResult?.Hash, Snackbar.LENGTH_LONG).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun addWithUI(callback: () -> NamedHash?) {

        val show = LoadToast(this).show()

        Thread(Runnable {
            addResult = try {
                callback()
            } catch (e: ConnectException) {
                null
            }
            runOnUiThread {
                val displayString: String
                if (addResult == null) {
                    show.error()
                    displayString = "could not execute ( daemon running? )" + ipfs.lastError?.Message
                } else {
                    show.success()
                    displayString = getSuccessDisplayHTML()

                    qr_src.setImageBitmap(QRCode.from(getSuccessURL()).bitmap())
                }

                Log.i(displayString)

                hashInfoText.text = Html.fromHtml(displayString)

            }
        }).start()

    }

    abstract fun getSuccessDisplayHTML(): String
    abstract fun getSuccessURL(): String
}
