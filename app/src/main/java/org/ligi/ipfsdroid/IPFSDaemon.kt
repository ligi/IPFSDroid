package org.ligi.ipfsdroid

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.support.v7.app.AlertDialog
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okio.Okio
import java.io.File
import java.io.FileNotFoundException

class IPFSDaemon(private val androidContext: Context) {

    private fun getBinaryFile() = File(androidContext.filesDir, "ipfsbin")
    private fun getRepoPath() = File(androidContext.filesDir, ".ipfs_repo")
    fun getVersionFile() = File(androidContext.filesDir, ".ipfs_version")

    fun isReady() = File(getRepoPath(), "version").exists()

    private fun getBinaryFileByABI(abi: String) = when {
        abi.toLowerCase().startsWith("x86") -> "x86"
        abi.toLowerCase().startsWith("arm") -> "arm"
        else -> "unknown"
    }

    fun download(activity: Activity,
                 runInit: Boolean,
                 afterDownloadCallback: () -> Unit) = async(UI) {

        val progressDialog = ProgressDialog(androidContext)
        progressDialog.setMessage("Copy IPFS binary")
        progressDialog.setCancelable(false)
        progressDialog.show()

        try {
            async(CommonPool) {
                downloadFile(activity)
                getBinaryFile().setExecutable(true)
            }.await()

            if (runInit) {
                progressDialog.setMessage("Running init")

                val readText = async(CommonPool) {
                    val exec = run("init")
                    exec.waitFor()

                    exec.inputStream.bufferedReader().readText() + exec.errorStream.bufferedReader().readText()
                }.await()

                AlertDialog.Builder(androidContext)
                        .setMessage(readText)
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
            }
            progressDialog.dismiss()

            afterDownloadCallback()

        } catch (e: FileNotFoundException) {
            progressDialog.dismiss()
            AlertDialog.Builder(androidContext)
                    .setMessage("Unsupported architecture " + Build.CPU_ABI)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()

        }
    }

    fun run(cmd: String): Process {
        val env = arrayOf("IPFS_PATH=" + getRepoPath().absoluteFile)
        val command = getBinaryFile().absolutePath + " " + cmd

        return Runtime.getRuntime().exec(command, env)
    }

    private fun downloadFile(activity: Activity) {

        val source = Okio.buffer(Okio.source(activity.assets.open(getBinaryFileByABI(Build.CPU_ABI))))
        val sink = Okio.buffer(Okio.sink(getBinaryFile()))
        while (!source.exhausted()) {
            source.read(sink.buffer(), 1024)
        }
        source.close()
        sink.close()

    }

}