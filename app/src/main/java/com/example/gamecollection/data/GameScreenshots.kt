package com.example.gamecollection.data

data class GameScreenshots (
    val results: List<GameScreenshot>
)

data class GameScreenshot (
    val image: String,
    val hidden: Boolean
)
