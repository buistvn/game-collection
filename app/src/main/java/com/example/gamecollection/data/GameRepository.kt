package com.example.gamecollection.data

import com.example.gamecollection.api.RAWGService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameListRepository(
    private val service: RAWGService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun loadGameList(
        key: String,
        search: String
    ) : Result<GameList> =
        withContext(ioDispatcher) {
            try {
                val list = service.getGameList(key, search)
                Result.success(list)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun loadGameDetails(
        id: Int,
        key: String
    ) : Result<GameDetails> =
        withContext(ioDispatcher) {
            try {
                val details = service.getGameDetails(id, key)
                Result.success(details)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
