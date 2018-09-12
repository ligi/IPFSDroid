package org.ligi.ipfsdroid

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.ipfs.kotlin.commands.Add
import io.ipfs.kotlin.commands.Get
import io.ipfs.kotlin.commands.Pins
import okhttp3.ResponseBody
import java.io.File
import java.io.InputStream

/**
 * Created by WillowTree on 8/31/18.
 */
fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun File.copyInputStreamToFile(inputStream: InputStream) {
    inputStream.use { input ->
        this.outputStream().use { fileOut ->
            input.copyTo(fileOut)
        }
    }
}

fun Pins.getPins(): String {
    return this.ipfs.callCmd("pin/ls").use(ResponseBody::string)
}

fun Add.unPin(hash: String) : String {
    return this.ipfs.callCmd("pin/rm/-r/$hash").use(ResponseBody::string)
}

fun Get.getFile(hash: String, filename: String): String {
    return this.ipfs.callCmd("get/$hash/-o=$filename").use(ResponseBody::string)
}