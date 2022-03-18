package com.example.gamecollection.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.io.Serializable

@Entity
data class GameListItem(
    @Json(name = "id")
    @PrimaryKey
    val id: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "released")
    val released: String?,

    @Json(name = "background_image")
    val background_image: String?
) : Serializable
