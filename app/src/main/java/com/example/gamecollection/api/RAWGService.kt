package com.example.gamecollection.api

import com.example.gamecollection.data.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RAWGService {
    // Get list of games on search
    @GET("/api/games")
    suspend fun getGameList(
        @Query("key") key: String,
        @Query("search") search: String?,
        @Query("dates") dates: String?,
        @Query("ordering") ordering: String?,
        @Query("page_size") page_size: String?,
        @Query("genres") genres: String?
    ) : GameList

    // Get game details on card click
    @GET("/api/games/{id}")
    suspend fun getGameDetails(
        @Path("id", encoded = true) id: Int,
        @Query("key") key: String
    ) : GameDetails

    // Get game screenshots in details
    @GET("/api/games/{game_pk}/screenshots")
    suspend fun getGameScreenshots(
        @Path("game_pk", encoded = true) game_pk: String,
        @Query("key") key: String
    ) : GameScreenshots

    // Get game trailers in details
    @GET("/api/games/{id}/movies")
    suspend fun getGameTrailers(
        @Path("id", encoded = true) id: Int,
        @Query("key") key: String
    ) : GameTrailer

    companion object {
        private const val BASE_URL = "https://api.rawg.io"

        fun create() : RAWGService {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return retrofit.create(RAWGService::class.java)
        }
    }
}
