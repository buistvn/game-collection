package com.example.gamecollection.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert

@Dao
interface GameListItemDao {
    @Insert
    suspend fun insert(game: GameListItem)

    @Delete
    suspend fun delete(game: GameListItem)
}
