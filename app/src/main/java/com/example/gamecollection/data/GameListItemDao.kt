package com.example.gamecollection.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameListItemDao {
    @Insert
    suspend fun insert(game: GameListItem)

    @Delete
    suspend fun delete(game: GameListItem)

    @Query("SELECT * FROM GameListItem")
    fun getAllGames(): Flow<List<GameListItem>>

    @Query("SELECT * FROM GameListItem WHERE id = :id LIMIT 1")
    fun getGameById(id: Int): Flow<GameListItem?>
}
