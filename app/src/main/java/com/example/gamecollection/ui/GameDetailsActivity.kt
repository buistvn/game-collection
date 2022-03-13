package com.example.gamecollection.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.gamecollection.R
import com.example.gamecollection.data.LoadingStatus
import com.google.android.material.progressindicator.CircularProgressIndicator

const val EXTRA_GAME_ID = "com.example.gamecollection.GAME_ID"

class GameDetailActivity : AppCompatActivity() {
    private val tag = "GameDetailActivity"
    private var gameID: Int? = null
    private val gameDetailsViewModel: GameDetailsViewModel by viewModels()

    private lateinit var detailsLayout: LinearLayout
    private lateinit var searchErrorTV: TextView
    private lateinit var loadingIndicator: CircularProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_details)

        detailsLayout = findViewById(R.id.details)
        searchErrorTV = findViewById(R.id.tv_search_error)
        loadingIndicator = findViewById(R.id.loading_indicator)

        if (intent != null && intent.hasExtra(EXTRA_GAME_ID)) {
            gameID = intent.getSerializableExtra(EXTRA_GAME_ID) as Int
            Log.d(tag, gameID!!.toString())
            gameDetailsViewModel.loadResults(gameID!!, RAWG_API_KEY)
        }
        gameDetailsViewModel.results.observe(this) { results ->
            if (results != null) {
                val short = results.description_raw.chunked(300)[0].split(".").toMutableList()
                short.removeLast()
                val desc = short.joinToString(".") + "..."
                Log.d(tag, results.description_raw)
                findViewById<TextView>(R.id.tv_game_title).text = results.name
                findViewById<TextView>(R.id.tv_description_short).text = desc
            }
        }
        gameDetailsViewModel.loading.observe(this) { uiState ->
            when(uiState){
                LoadingStatus.LOADING -> {
                    loadingIndicator.visibility = View.VISIBLE
                    detailsLayout.visibility = View.INVISIBLE
                    searchErrorTV.visibility = View.INVISIBLE
                }
                LoadingStatus.ERROR -> {
                    loadingIndicator.visibility = View.INVISIBLE
                    detailsLayout.visibility = View.INVISIBLE
                    searchErrorTV.visibility = View.VISIBLE
                }
                else -> {
                    loadingIndicator.visibility = View.INVISIBLE
                    detailsLayout.visibility = View.VISIBLE
                    searchErrorTV.visibility = View.INVISIBLE
                }
            }
        }
        gameDetailsViewModel.error.observe(this) { error ->
            Log.d(tag, error.toString())
        }
    }
}