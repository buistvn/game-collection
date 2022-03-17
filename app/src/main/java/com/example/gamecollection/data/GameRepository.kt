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
        genres: String?,
        developers: String?
    ) : Result<GameList> =
        withContext(ioDispatcher) {
            try {
                val list = service.getGameList(key, search, dates, ordering, page_size, genres, developers)
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
    suspend fun loadGameScreenshots(
        game_pk: String,
        key: String
    ) : Result<GameScreenshots> =
        withContext(ioDispatcher) {
            try {
                val screenshots = service.getGameScreenshots(game_pk, key)
                Result.success(screenshots)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    suspend fun loadGameTrailers(
        id: Int,
        key: String
    ) : Result<GameTrailer> =
        withContext(ioDispatcher) {
            try {
                val trailer = service.getGameTrailers(id, key)
                Result.success(trailer)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun loadDeveloperDetails(
        id: Int,
        key: String
    ): Result<DeveloperDetails> =
    withContext(ioDispatcher) {
        try {
            val details = service.getDeveloperDetails(id, key)
            Result.success(details)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
