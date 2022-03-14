package com.example.gamecollection.data

data class GameDetails(
    val id: Int,
    val name: String,
    val description_raw: String,
    val released: String,
    val background_image: String,
    val rating: Float,
    val stores: List<Store>,
    val developers: List<Developer>,
    val genres: List<Genre>,
    val tags: List<Tag>,
    val esrb_rating: ESRBRating?
)

data class Store(
    val store: StoreDetails
)

data class StoreDetails(
    val name: String,
    val domain: String
)

data class Developer(
    val id: Int,
    val name: String
)

data class Genre(
    val name: String
)

data class Tag(
    val name: String
)

data class ESRBRating(
    val name: String
)
