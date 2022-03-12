package com.example.gamecollection.data

import com.squareup.moshi.Json
import java.io.Serializable

data class GameListItem(
    @Json(name = "id")val id: Int,
    @Json(name = "name")val name: String,
    @Json(name = "released")val released: String,
    @Json(name = "background_image")val background_image: String
):Serializable
