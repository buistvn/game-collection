package com.example.gamecollection.data

import com.squareup.moshi.Json
import java.io.Serializable

data class DeveloperDetails(
    @Json(name = "id")val id: Int,
    @Json(name = "name")val name: String,
    @Json(name = "games_count")val games: Int,
    @Json(name = "image_background")val background_image: String?,
    @Json(name = "description")val description: String
): Serializable
