package com.example.gamecollection.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamecollection.R
import com.example.gamecollection.data.GameListItem
import com.example.gamecollection.data.LoadingStatus
import com.google.android.material.progressindicator.CircularProgressIndicator

const val EXTRA_DEVELOPER_ID = "com.example.gamecollection.DEVELOPER_ID"

class DeveloperDetailsFragment : Fragment(R.layout.developer_details) {
    private val TAG = "DeveloperDetailsFragment"

    private val args: DeveloperDetailsFragmentArgs by navArgs()

    private val gameSearchViewModel: GameSearchViewModel by viewModels()
    private val developerViewModel: DeveloperDetailsViewModel by viewModels()

    private lateinit var gameListAdapter: GameListAdapter

    private lateinit var searchResultListRV: RecyclerView
    private lateinit var loadingIndicator: CircularProgressIndicator
    private lateinit var searchErrorTV: TextView
    private lateinit var holder: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingIndicator = view.findViewById(R.id.developer_loading_indicator)
        searchResultListRV = view.findViewById(R.id.developer_RV)
        searchErrorTV = view.findViewById(R.id.developer_search_error)
        holder = view.findViewById(R.id.developer_view)

        gameListAdapter = GameListAdapter(::onGameListItemClick)

        searchResultListRV.layoutManager = GridLayoutManager(requireContext(), 2)
        searchResultListRV.setHasFixedSize(true)
        searchResultListRV.adapter = gameListAdapter

        gameSearchViewModel.results.observe(viewLifecycleOwner) { results ->
            gameListAdapter.updateGameListItems(results?.results)
        }

        developerViewModel.loadResults(args.developerId, RAWG_API_KEY)

        developerViewModel.results.observe(viewLifecycleOwner) { results ->
            if (results != null) {
                val games = results.games
                view.findViewById<TextView>(R.id.Developer_Name).text = results.name
                view.findViewById<TextView>(R.id.developer_games).text = "Total Games by this Developer: \t#"+ games.toString()

                Glide.with(this)
                    .load(results.background_image)
                    .into(view.findViewById(R.id.dev_image))

                if (results.description.isNullOrEmpty()) {
                    view.findViewById<TextView>(R.id.Developer_Details).text = ""
                    view.findViewById<TextView>(R.id.Developer_Details).visibility = View.INVISIBLE
                }
                else {
                    val desc: TextView = view.findViewById(R.id.Developer_Details)
                    val long = HtmlCompat.fromHtml(results.description, HtmlCompat.FROM_HTML_MODE_COMPACT)
                    val size = if (long.length < 200) long.length else 200
                    val temp = long.chunked(size)[0].split(".").toMutableList()
                    val shortI = temp.joinToString(".")
                    val short = if (shortI.compareTo(long.toString()) != 0) ("$shortI...") else long
                    desc.text = short
                    desc.setOnClickListener {
                        if (desc.text == short) {
                            desc.text = long
                        }
                        else {
                            desc.text = short
                        }
                    }
                }

                if (games > 20) {
                    gameSearchViewModel.loadResults(
                        RAWG_API_KEY,
                        null,
                        null,
                        null,
                        "20",
                        null,
                        args.developerId.toString()
                    )
                }
                else if (games <= 2 || games %2 == 0) {
                    gameSearchViewModel.loadResults(
                        RAWG_API_KEY,
                        null,
                        null,
                        null,
                        games.toString(),
                        null,
                        args.developerId.toString()
                    )
                }
                else {
                    gameSearchViewModel.loadResults(
                        RAWG_API_KEY,
                        null,
                        null,
                        null,
                        (games - 1).toString(),
                        null,
                        args.developerId.toString()
                    )
                }
            }
        }

        gameSearchViewModel.loading.observe(viewLifecycleOwner) { loading->
            when (loading) {
                LoadingStatus.LOADING -> {
                    loadingIndicator.visibility = View.VISIBLE
                    holder.visibility = View.INVISIBLE
                    searchErrorTV.visibility = View.INVISIBLE
                }
                LoadingStatus.ERROR -> {
                    loadingIndicator.visibility = View.INVISIBLE
                    holder.visibility = View.VISIBLE
                    searchErrorTV.visibility = View.VISIBLE
                }
                else -> {
                    loadingIndicator.visibility = View.INVISIBLE
                    holder.visibility = View.VISIBLE
                    searchErrorTV.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun onGameListItemClick(gameListItem: GameListItem) {
        val directions = DeveloperDetailsFragmentDirections.navigateToGameDetails(gameListItem)
        findNavController().navigate(directions)
    }
}
