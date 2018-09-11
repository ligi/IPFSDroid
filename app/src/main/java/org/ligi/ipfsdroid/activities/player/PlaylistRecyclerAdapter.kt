package org.ligi.ipfsdroid.activities.player

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.play_list_view_item.view.*
import org.ligi.ipfsdroid.R
import org.ligi.ipfsdroid.inflate
import org.ligi.ipfsdroid.repository.PlaylistItem

/**
 * Created by WillowTree on 9/11/18.
 */
class PlaylistRecyclerAdapter(val items: List<PlaylistItem>) : RecyclerView.Adapter<PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val inflatedView = parent.inflate(R.layout.play_list_view_item, false)
        return PlaylistViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        // TODO add title and description to playlist items
        holder.nameText.text = items[position].hash
        holder.descriptionText.text = items[position].fileName

        // TODO if the current item is 0 in the playlist, give it a different layout - alternatively, only add items 1-end to the recyclerview and put the 0 item in a static view at the top of the player
    }

}

class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val nameText = view.textViewName
    val descriptionText = view.textViewDescription
}