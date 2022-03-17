package com.example.gamecollection.ui

import android.app.GameManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamecollection.R
import com.example.gamecollection.data.GameList
import com.example.gamecollection.data.GameListItem

class GameListAdapter (private val onGameListItemClick: (GameListItem) -> Unit)
    : RecyclerView.Adapter<GameListAdapter.ViewHolder>() {
    var gameListItems = listOf<GameListItem>()

    fun updateGameListItems(newGameListItems: List<GameListItem>?) {
        gameListItems = newGameListItems ?: listOf()
        notifyDataSetChanged()
    }

    override fun getItemCount() = this.gameListItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.game_list_item, parent, false)
        return ViewHolder(itemView, onGameListItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this.gameListItems[position])
    }

    class ViewHolder(itemView: View, private val onGameListItemClick: (GameListItem) -> Unit)
        : RecyclerView.ViewHolder(itemView) {
        private val nameTV: TextView = itemView.findViewById(R.id.tv_name)
        private val bgImageIV: ImageView = itemView.findViewById(R.id.iv_background_image)

        private var currentGameListItem: GameListItem? = null

        init {
            itemView.setOnClickListener {
                currentGameListItem?.let(onGameListItemClick)
            }
        }

        fun bind(gameListItem: GameListItem) {
            currentGameListItem = gameListItem

            val ctx = itemView.context
            nameTV.text = gameListItem.name

            Glide.with(ctx)
                .load(gameListItem.background_image)
                .into(bgImageIV)
        }
    }
}
