package com.example.gamecollection.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamecollection.R
import com.example.gamecollection.data.GameListItem
import com.example.gamecollection.data.LoadingStatus
import com.google.android.material.progressindicator.CircularProgressIndicator

const val EXTRA_GAME_ID = "com.example.gamecollection.GAME_ID"

class GameDetailActivity : AppCompatActivity() {
    private val tag = "GameDetailActivity"
    private var gameID: Int? = null
    private val gameDetailsViewModel: GameDetailsViewModel by viewModels()
    private val gameSearchViewModel: GameSearchViewModel by viewModels()
    private lateinit var gameListAdapter: GameListAdapter

    private lateinit var detailsLayout: LinearLayout
    private lateinit var searchErrorTV: TextView
    private lateinit var loadingIndicator: CircularProgressIndicator
    private lateinit var searchResultListRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_details)

        detailsLayout = findViewById(R.id.details)
        searchErrorTV = findViewById(R.id.tv_search_error)
        loadingIndicator = findViewById(R.id.loading_indicator)
        searchResultListRV = findViewById(R.id.rv_search_results)

        gameListAdapter = GameListAdapter(::onGameListClick)
        searchResultListRV.layoutManager = GridLayoutManager(this,2)
        searchResultListRV.setHasFixedSize(true)

        searchResultListRV.adapter = gameListAdapter

        gameSearchViewModel.results.observe(this) { results ->
            gameListAdapter.updateRepoList(results)
        }

        if (intent != null && intent.hasExtra(EXTRA_GAME_ID)) {
            gameID = intent.getSerializableExtra(EXTRA_GAME_ID) as Int
            Log.d(tag, gameID!!.toString())
            gameDetailsViewModel.loadResults(gameID!!, RAWG_API_KEY)
        }
        gameDetailsViewModel.results.observe(this) { results ->
            if (results != null) {
                var x = 0
                var ids = ""
                var genres = "Genres: "
                for (genre in results.genres) {
                    genres = genres + genre!!.name + ", "
                    if (x < 3) {
                        ids += results.genres[x]!!.id.toString() + ", "
                    }
                    x += 1
                }
                ids = ids.dropLast(2)
                genres = genres.dropLast(2)
                gameSearchViewModel.loadResults(RAWG_API_KEY, null, null, null, "4", ids)
                findViewById<TextView>(R.id.tv_game_title).text = results.name

                val rating = "Rating: " + results.rating.toString() + "/5"
                findViewById<TextView>(R.id.tv_rating).text = rating

                val releasedDate = "Released: " + results.released
                findViewById<TextView>(R.id.tv_released).text = releasedDate

                findViewById<TextView>(R.id.tv_genres).text = genres

                val tags: TextView = findViewById(R.id.tv_tags)
                if (results.tags.isNullOrEmpty()) {
                    tags.text = "No tags"
                } else {
                    var fullTags = "Tags: "
                    results.tags.forEach {
                        fullTags += it!!.name + ", "
                    }
                    fullTags = fullTags.dropLast(2)
                    var fourTags = "Tags: "
                    for ((n, tags) in results.tags.withIndex()) {
                        if (n < 4) fourTags += tags!!.name + ", "
                    }
                    fourTags = fourTags.dropLast(2) + "..."
                    tags.text = fourTags
                    tags.setOnClickListener {
                        if (tags.text == fourTags) {
                            tags.text = fullTags
                        } else {
                            tags.text = fourTags
                        }
                    }
                }

                val desc: TextView = findViewById(R.id.tv_description)
                val long = HtmlCompat.fromHtml(results.description, HtmlCompat.FROM_HTML_MODE_COMPACT)
                val size = if (long.length < 300) long.length else 300
                val temp = long.chunked(size)[0].split(".").toMutableList()
                val shortI = temp.joinToString(".")
                val short = if (shortI.compareTo(long.toString()) != 0) ("$shortI...") else long
                desc.text = short
                desc.setOnClickListener {
                    if (desc.text == short) {
                        desc.text = long
                    } else {
                        desc.text = short
                    }
                }
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
    private fun onGameListClick(gameListItem: GameListItem) {
        Log.d(tag, gameListItem.toString())
        val intent = Intent(this, GameDetailActivity::class.java).apply {
            putExtra(EXTRA_GAME_ID,gameListItem.id)
        }
        startActivity(intent)
    }
}