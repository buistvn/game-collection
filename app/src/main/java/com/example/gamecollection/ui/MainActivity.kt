package com.example.gamecollection.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamecollection.R
import com.example.gamecollection.data.GameListItem
import com.example.gamecollection.data.LoadingStatus
import com.google.android.material.progressindicator.CircularProgressIndicator
import androidx.preference.PreferenceManager

const val RAWG_API_KEY = "e1dd3dd1ae1b47a49ae5b110b5447c6c"

class MainActivity : AppCompatActivity() {
    private val tag = "MainActivity"

    private val gameSearchViewModel: GameSearchViewModel by viewModels()
    private lateinit var gameListAdapter: GameListAdapter

    private lateinit var searchInputET: EditText
    private lateinit var searchResultListRV: RecyclerView
    private lateinit var searchErrorTV: TextView
    private lateinit var loadingIndicator: CircularProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchInputET = findViewById(R.id.et_search_input)
        searchResultListRV = findViewById(R.id.rv_search_results)
        searchErrorTV = findViewById(R.id.tv_search_error)
        loadingIndicator = findViewById(R.id.loading_indicator)

        gameListAdapter = GameListAdapter(::onGameListItemClick)

        searchResultListRV.layoutManager = GridLayoutManager(this,2)
        searchResultListRV.setHasFixedSize(true)

        searchResultListRV.adapter = gameListAdapter

        gameSearchViewModel.results.observe(this) { results ->
            gameListAdapter.updateGameListItems(results?.results)
        }

        gameSearchViewModel.loading.observe(this) { loading->
            when(loading) {
                LoadingStatus.LOADING -> {
                    loadingIndicator.visibility = View.VISIBLE
                    searchResultListRV.visibility = View.INVISIBLE
                    searchErrorTV.visibility = View.INVISIBLE
                }
                LoadingStatus.ERROR -> {
                    loadingIndicator.visibility = View.INVISIBLE
                    searchResultListRV.visibility = View.INVISIBLE
                    searchErrorTV.visibility = View.VISIBLE
                }
                else -> {
                    loadingIndicator.visibility = View.INVISIBLE
                    searchResultListRV.visibility = View.VISIBLE
                    searchErrorTV.visibility = View.INVISIBLE
                }
            }
        }

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        val searchButton: Button = findViewById(R.id.bt_search)
        searchButton.setOnClickListener {
            val searchQuery = searchInputET.text.toString()

            if (!TextUtils.isEmpty(searchQuery)) {
                searchInputET.text.clear()
                var sort = sharedPrefs.getString(
                    getString(R.string.pref_sort_key),
                    null
                )
                val beginYear = sharedPrefs.getString(
                    getString(R.string.pref_begin_year_key),
                    null
                )

                val endYear = sharedPrefs.getString(
                    getString(R.string.pref_end_year_key),
                    null
                )

                val year = "$beginYear-01-01,$endYear-12-31"
                if (sort == "none") sort = null
                if (!isEmpty(beginYear) && !isEmpty(endYear)) {
                    // Results on search are from the user's input
                    gameSearchViewModel.loadResults(
                        RAWG_API_KEY,
                        searchQuery,
                        year,
                        sort,
                        "30",
                        null,
                        null
                    )
                }
                else {
                    // Results on search are from the user's input
                    gameSearchViewModel.loadResults(
                        RAWG_API_KEY,
                        searchQuery,
                        null,
                        sort,
                        "30",
                        null,
                        null
                    )
                }
            }
        }

        var sort = sharedPrefs.getString(
            getString(R.string.pref_sort_key),
            null
        )
        var beginYear = sharedPrefs.getString(
            getString(R.string.pref_begin_year_key),
            null
        )

        var endYear = sharedPrefs.getString(
            getString(R.string.pref_end_year_key),
            null
        )

        if (beginYear == null || beginYear == "") { beginYear = "2021" }
        if (endYear == null || endYear == "") { endYear = "2021" }

        val year = "$beginYear-01-01,$endYear-12-31"
        if (sort == "none") sort = null
        // Results on start are the most popular games in 2021
        gameSearchViewModel.loadResults(
            RAWG_API_KEY,
            null,
            year,
            sort,
            "30",
            null,
            null
        )
    }

    override fun onResume() {
        Log.d(tag, "onResume()")
        super.onResume()
    }

    override fun onPause() {
        Log.d(tag, "onPause()")
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorites -> {
                val intent = Intent(this, FavoriteGamesActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onGameListItemClick(gameListItem: GameListItem) {
        Log.d(tag, gameListItem.toString())
        val intent = Intent(this, GameDetailActivity::class.java).apply {
            putExtra(EXTRA_GAME_LIST_ITEM, gameListItem)
        }
        startActivity(intent)
    }
}
