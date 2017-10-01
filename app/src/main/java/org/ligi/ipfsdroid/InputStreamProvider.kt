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

    fun fromURI(context: Context, uri: Uri) = when (uri.scheme) {
        "content" -> fromContent(context, uri)
        "http", "https" -> fromOKHttp(uri) // TODO check if SPDY should be here
        else -> getDefaultInputStreamForUri(uri) // eg "file"
    }

    private fun fromOKHttp(uri: Uri): InputStreamProvider.InputStreamWithSource? {
        try {
            val client = OkHttpClient()
            val url = URL(uri.toString())
            val requestBuilder = Request.Builder().url(url)

            val request = requestBuilder.build()

            val response = client.newCall(request).execute()

            val body = response.body()
            if (body != null) {
                return InputStreamProvider.InputStreamWithSource(uri.toString(), body.byteStream())
            }
        } catch (e: MalformedURLException) {
        } catch (e: IOException) {
        }

        return null
    }

    private fun fromContent(ctx: Context, uri: Uri) = try {
        InputStreamProvider.InputStreamWithSource(uri.toString(), ctx.contentResolver.openInputStream(uri))
    } catch (e: FileNotFoundException) {
        null
    }

    private fun getDefaultInputStreamForUri(uri: Uri) = try {
        InputStreamProvider.InputStreamWithSource(uri.toString(), BufferedInputStream(URL(uri.toString()).openStream(), 4096))
    } catch (e: IOException) {
        null
    }

}
