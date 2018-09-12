package org.ligi.ipfsdroid.activities.player

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.play_list_view_item.view.*
import org.ligi.ipfsdroid.R
import org.ligi.ipfsdroid.inflate
import org.ligi.ipfsdroid.repository.PlaylistItem
import org.ligi.ipfsdroid.repository.Repository

/**
 * Created by WillowTree on 9/11/18.
 */
class PlaylistRecyclerAdapter(val items: List<PlaylistItem>, val repository: Repository) : RecyclerView.Adapter<PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val inflatedView = parent.inflate(R.layout.play_list_view_item, false)
        return PlaylistViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.nameText.text = items[position].name
        holder.descriptionText.text = items[position].description
        holder.deleteButton.setOnClickListener {
            repository.deletePlaylistItem(items[position])
        }

        if (position == 0) {
            holder.itemView.setBackgroundColor(holder.itemView.context.resources.getColor(R.color.color_accent))
            holder.deleteButton.visibility = View.GONE
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.context.resources.getColor(R.color.background_floating_material_light))
        }
    }

}

class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val nameText = view.textViewName
    val descriptionText = view.textViewDescription
    val deleteButton = view.deletePlayListItem
}