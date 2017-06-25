package org.ligi.ipfsdroid

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.support.v7.app.AlertDialog
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Okio
import java.io.File
import java.util.concurrent.TimeUnit

class IPFSDaemon(val context: Context) {

    private fun getBinaryFile() = File(context.filesDir, "ipfsbin")
    private fun getRepoPath() = File(context.filesDir, ".ipfs_repo")

    fun isReady(): Boolean {
        return File(getRepoPath(), "version").exists()
    }

    private fun getBinaryHashByABI(abi: String): String? {
        if (abi.startsWith("x86")) {
            return "QmcF3vtdEVLAM2fVpQPQn38RWyK5sEZ8TPVtTKjcszcK6t"
        } else if (abi.startsWith("arm")) {
            return "Qmbq23LxgodGVQ6aLuj9qSLrxAMXkMCF3fVU5TjdTxG3Ak"
        }
        return null
    }


    fun download(activity: Activity, f: () -> Unit) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Downloading ipfs binary")
        progressDialog.setCancelable(false)
        progressDialog.show()

        Thread(Runnable {

            downloadFile(activity, progressDialog)
            getBinaryFile().setExecutable(true)

            activity.runOnUiThread {
                progressDialog.setMessage("Running init")
            }

            val exec = run("init")
            exec.waitFor()

            var readText = exec.inputStream.bufferedReader().readText()
            readText += exec.errorStream.bufferedReader().readText()

            activity.runOnUiThread {
                progressDialog.dismiss()
                AlertDialog.Builder(context)
                        .setMessage(readText)
                        .setPositiveButton("ok", null)
                        .show()
                f();
            }

        }).start()
    }

    fun run(cmd: String): Process {
        val env = arrayOf("IPFS_PATH=" + getRepoPath().absoluteFile)
        val command = getBinaryFile().absolutePath + " " + cmd
        val exec = Runtime.getRuntime().exec(command, env)

        return exec
    }

    private fun downloadFile(activity: Activity, progressDialog: ProgressDialog) {
        val build = Request.Builder().url("http://ipfs.io/ipfs/" + getBinaryHashByABI(Build.CPU_ABI)).build()

        val okHttpClient = OkHttpClient.Builder().readTimeout(200, TimeUnit.SECONDS).build()
        val responseBody = okHttpClient.newCall(build).execute().body()

        val source = responseBody.source();

        val buffer = Okio.buffer(Okio.sink(getBinaryFile()))

        var i = 0L;

        while (!source.exhausted()) {

            i += source.read(buffer.buffer(), 1024);

            activity.runOnUiThread {
                val s = i / 1024
                progressDialog.setMessage("$s kB")
            }
        }

        buffer.close()
    }

}