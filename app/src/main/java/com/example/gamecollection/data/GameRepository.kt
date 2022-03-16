package com.example.gamecollection.data

import android.util.Log
import com.example.gamecollection.api.RAWGService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository(
    private val service: RAWGService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun loadGameList(
        key: String,
        search: String?,
        dates: String?,
        ordering: String?,
        page_size: String?,
        genres: String?
    ) : Result<GameList> =
        withContext(ioDispatcher) {
            try {
                val list = service.getGameList(key, search, dates, ordering, page_size, genres)
                Log.d("GameRepository", list.toString())
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
