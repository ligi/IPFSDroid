package org.ligi.ipfsdroid

import com.chibatching.kotpref.KotprefModel

class State : KotprefModel() {
    var modeInt: Int by intPrefVar()

    fun setByMode(newMode:IPFSConnectionMode) {
        modeInt = newMode.integerRepresentation
    }

    fun getMode() : IPFSConnectionMode {
        return IPFSConnectionMode.getModeByIntegerRepresentation(modeInt)
    }
}
