package com.example.gamecollection.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamecollection.R
import com.example.gamecollection.data.GameListItem

class FavoriteGamesActivity : AppCompatActivity() {
    private var gameListAdapter = GameListAdapter(::onGameListItemClick)
    private val favoriteGamesViewModel: FavoriteGamesViewModel by viewModels()

    private lateinit var favoriteGamesRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_games)

        favoriteGamesRV = findViewById(R.id.rv_favorite_games)

        favoriteGamesRV.layoutManager = GridLayoutManager(this, 2)
        favoriteGamesRV.setHasFixedSize(true)

        favoriteGamesRV.adapter = gameListAdapter

        favoriteGamesViewModel.favoriteGames.observe(this) { favoriteGames ->
            gameListAdapter.updateGameListItems(favoriteGames)
        }
    }

    private fun onGameListItemClick(gameListItem: GameListItem) {
        val intent = Intent(this, GameDetailActivity::class.java).apply {
            putExtra(EXTRA_GAME_ID,gameListItem.id)
        }
        startActivity(intent)
    }
}
