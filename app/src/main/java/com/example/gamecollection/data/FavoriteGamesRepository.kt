package com.example.gamecollection.data

class FavoriteGamesRepository(
    private val dao: GameListItemDao
) {
    suspend fun insertFavoriteGame(game: GameListItem) = dao.insert(game)

    suspend fun removeFavoriteGame(game: GameListItem) = dao.delete(game)

    fun getAllFavoriteGames() = dao.getAllGames()

    fun getFavoriteGameById(id: Int) = dao.getGameById(id)
}
