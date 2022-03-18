package com.example.gamecollection.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamecollection.R
import com.example.gamecollection.data.GameListItem

class FavoriteGamesFragment : Fragment(R.layout.favorite_games) {
    private var gameListAdapter = GameListAdapter(::onGameListItemClick)
    private val favoriteGamesViewModel: FavoriteGamesViewModel by viewModels()

    private lateinit var favoriteGamesRV: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteGamesRV = view.findViewById(R.id.rv_favorite_games)

        favoriteGamesRV.layoutManager = GridLayoutManager(requireContext(), 2)
        favoriteGamesRV.setHasFixedSize(true)
        favoriteGamesRV.adapter = gameListAdapter

        favoriteGamesViewModel.favoriteGames.observe(viewLifecycleOwner) { favoriteGames ->
            gameListAdapter.updateGameListItems(favoriteGames)
        }
    }

    private fun onGameListItemClick(gameListItem: GameListItem) {
        val directions = FavoriteGamesFragmentDirections.navigateToGameDetails(gameListItem)
        findNavController().navigate(directions)
    }
}
