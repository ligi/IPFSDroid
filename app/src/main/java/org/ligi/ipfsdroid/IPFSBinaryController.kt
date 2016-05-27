package org.ligi.ipfsdroid

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import java.io.File
import java.io.FileOutputStream

class IPFSBinaryController(val context: Context) {

    fun copy() {
        val calculateFilename = calculateFilename()
        if (calculateFilename != null) {
            doCopyForArch(calculateFilename)
        } else {
            AlertDialog.Builder(context).setMessage("no supported ABIs").show()
        }
    }

    private fun calculateFilename(): String? {
        if (Build.CPU_ABI.startsWith("x86")) {
            return "ipfs-android-16-386"
        } else if (Build.CPU_ABI.startsWith("arm")) {
            return "ipfs-android-16-arm"
        }
        return null
    }

    private fun doCopyForArch(s: String) {
        val out = FileOutputStream(getFile())
        context.assets.open(s).copyTo(out)
        out.close()
        getFile().setExecutable(true)
    }

    fun run(cmd: String): String {
        val env = arrayOf("IPFS_PATH=" + File(context.filesDir, ".ipfs").absoluteFile)
        val process = Runtime.getRuntime().exec(getFile().absolutePath + " " + cmd, env)

        val err = process.errorStream.reader().readText()

        if (!err.isEmpty()) {
            return err
        }
        return process.inputStream.reader().readText()
    }

    fun getFile() = File(context.filesDir, "ipfs_bin")
}