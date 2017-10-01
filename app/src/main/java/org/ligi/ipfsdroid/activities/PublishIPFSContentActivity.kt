package org.ligi.ipfsdroid.activities

import android.os.Bundle
import io.ipfs.kotlin.model.NamedHash

class PublishIPFSContentActivity : HashTextAndBarcodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addWithUI {
            val result = ipfs.name.publish(intent.extras.getString("HASH"))
            return@addWithUI if (result == null) {
                null
            } else {
                NamedHash(result, result)
            }
        }
    }

    override fun getSuccessDisplayHTML()
            = "published <a href='${getSuccessURL()}'>${getSuccessURL()}</a>"

    override fun getSuccessURL() = "fs:/ipns/${addResult!!.Hash}"

}
