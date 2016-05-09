package org.ligi.ipfsdroid

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import java.io.File
import java.io.FileOutputStream

class IPFSBinaryController(val context: Context) {

    fun copy() {

        if (Build.SUPPORTED_ABIS.contains("x86")) {
            doCopyForArch("ipfs-android-16-386")
        } else if (Build.SUPPORTED_ABIS.contains("armeabi")) {
            doCopyForArch("ipfs-android-16-arm")
        } else {
            AlertDialog.Builder(context).setMessage("no supported ABIs").show()
        }
    }

    private fun doCopyForArch(s: String) {
        val out = FileOutputStream(getFile())
        context.assets.open(s).copyTo(out)
        out.close()
        getFile().setExecutable(true)
    }

    fun run(cmd: String): String {
        val env = arrayOf("IPFS_PATH="+File(context.filesDir,".ipfs").absoluteFile)
        val process = Runtime.getRuntime().exec(getFile().absolutePath + " " + cmd,env)

        val err = process.errorStream.reader().readText()

        if (!err.isEmpty()) {
            return err
        }
        return  process.inputStream.reader().readText()
    }

    fun getFile() = File(context.filesDir, "ipfs_bin")
}