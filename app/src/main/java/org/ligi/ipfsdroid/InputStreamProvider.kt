package org.ligi.ipfsdroid

import android.content.Context
import android.net.Uri
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL

object InputStreamProvider {

    class InputStreamWithSource(val source: String, val inputStream: InputStream)

    fun fromURI(context: Context, uri: Uri): InputStreamWithSource? {
        when (uri.scheme) {
            "content" ->

                return InputStreamProvider.fromContent(context, uri)

            "http", "https" ->
                // TODO check if SPDY should be here
                return InputStreamProvider.fromOKHttp(uri)

        // eg "file"
            else -> return InputStreamProvider.getDefaultInputStreamForUri(uri)
        }

    }

    fun fromOKHttp(uri: Uri): InputStreamWithSource? {
        try {
            val client = OkHttpClient()
            val url = URL(uri.toString())
            val requestBuilder = Request.Builder().url(url)

            val request = requestBuilder.build()

            val response = client.newCall(request).execute()

            return InputStreamWithSource(uri.toString(), response.body().byteStream())
        } catch (e: MalformedURLException) {
        } catch (e: IOException) {
        }

        return null
    }

    fun fromContent(ctx: Context, uri: Uri): InputStreamWithSource? {
        try {
            return InputStreamWithSource(uri.toString(), ctx.contentResolver.openInputStream(uri))
        } catch (e: FileNotFoundException) {
            return null
        }

    }


    fun getDefaultInputStreamForUri(uri: Uri): InputStreamWithSource? {
        try {
            return InputStreamWithSource(uri.toString(), BufferedInputStream(URL(uri.toString()).openStream(), 4096))
        } catch (e: IOException) {
            return null
        }

    }
}
