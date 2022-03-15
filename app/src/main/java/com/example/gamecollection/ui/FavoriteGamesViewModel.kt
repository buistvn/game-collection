package com.example.gamecollection.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.gamecollection.data.AppDatabase
import com.example.gamecollection.data.FavoriteGamesRepository
import com.example.gamecollection.data.GameListItem
import kotlinx.coroutines.launch

class FavoriteGamesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FavoriteGamesRepository(
        AppDatabase.getInstance(application).gameListItemDao()
    )

    val favoriteGames = repository.getAllFavoriteGames().asLiveData()

    fun addFavoriteGame(game: GameListItem) {
        viewModelScope.launch {
            repository.insertFavoriteGame(game)
        }
    }

    fun removeFavoriteGame(game: GameListItem) {
        viewModelScope.launch {
            repository.removeFavoriteGame(game)
        }
    }

    fun getFavoriteGameById(id: Int) =
        repository.getFavoriteGameById(id).asLiveData()
}
