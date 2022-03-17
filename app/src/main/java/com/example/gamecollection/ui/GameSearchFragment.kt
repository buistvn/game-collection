package com.example.gamecollection.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamecollection.R
import com.example.gamecollection.data.GameListItem
import com.example.gamecollection.data.LoadingStatus
import com.google.android.material.progressindicator.CircularProgressIndicator

class GameSearchFragment : Fragment(R.layout.game_search) {
    private val TAG = "GameSearchFragment"

    private val gameSearchViewModel: GameSearchViewModel by viewModels()

    private lateinit var gameListAdapter: GameListAdapter

    private lateinit var searchInputET: EditText
    private lateinit var searchResultListRV: RecyclerView
    private lateinit var searchErrorTV: TextView
    private lateinit var loadingIndicator: CircularProgressIndicator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchInputET = view.findViewById(R.id.et_search_input)
        searchResultListRV = view.findViewById(R.id.rv_search_results)
        searchErrorTV = view.findViewById(R.id.tv_search_error)
        loadingIndicator = view.findViewById(R.id.loading_indicator)

        gameListAdapter = GameListAdapter(::onGameListItemClick)

        searchResultListRV.layoutManager = GridLayoutManager(requireContext(), 2)
        searchResultListRV.setHasFixedSize(true)
        searchResultListRV.adapter = gameListAdapter

        gameSearchViewModel.results.observe(viewLifecycleOwner) { results ->
            gameListAdapter.updateGameListItems(results?.results)
        }

        gameSearchViewModel.loading.observe(viewLifecycleOwner) { loading->
            when (loading) {
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

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val searchButton: Button = view.findViewById(R.id.bt_search)
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
                if (!TextUtils.isEmpty(beginYear) && !TextUtils.isEmpty(endYear)) {
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

        // Results on start are the most popular games in 2021
        gameSearchViewModel.loadResults(
            RAWG_API_KEY,
            null,
            "2021-01-01,2021-12-31",
            "-added",
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

    private fun onGameListItemClick(gameListItem: GameListItem) {

    }
}
