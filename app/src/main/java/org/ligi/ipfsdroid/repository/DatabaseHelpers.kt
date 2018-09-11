package org.ligi.ipfsdroid.repository

/**
 * Created by WillowTree on 9/11/18.
 */

fun getDestinationIndex(allItems: List<PlaylistItem>, targetIndex: Int) : Double {
    if (targetIndex < allItems.size) {
        return when(targetIndex) {
            -1 -> allItems.size.toDouble() + 1
            0 -> (0 + allItems[0].index) / 2
            else -> (allItems[targetIndex - 1].index + allItems[targetIndex + 1].index) / 2
        }
    }
    return -1.0
}
