package com.example.gamecollection.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamecollection.R
import com.example.gamecollection.api.RAWGService
import com.example.gamecollection.data.GameListItem
import com.example.gamecollection.data.GameRepository
import com.example.gamecollection.data.LoadingStatus
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

const val EXTRA_DEVELOPER_ID = "com.example.gamecollection.DEVELOPER_ID"

class DeveloperDetailsActivity : AppCompatActivity() {
    private val tag = "DeveloperDetailActivity"
    private val gameSearchViewModel: GameSearchViewModel by viewModels()
    private val developerViewModel: DeveloperDetailsViewModel by viewModels()
    private lateinit var gameListAdapter: GameListAdapter
    private var developerID: Int? = null

    private lateinit var searchResultListRV: RecyclerView
    private lateinit var loadingIndicator: CircularProgressIndicator
    private lateinit var searchErrorTV: TextView
    private lateinit var holder: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer_details)

        loadingIndicator = findViewById(R.id.developer_loading_indicator)
        searchResultListRV = findViewById(R.id.developer_RV)
        gameListAdapter = GameListAdapter(::onGameListClick)
        searchErrorTV = findViewById(R.id.developer_search_error)
        holder = findViewById(R.id.developer_view)

        searchResultListRV.layoutManager = GridLayoutManager(this,2)
        searchResultListRV.setHasFixedSize(true)
        searchResultListRV.adapter = gameListAdapter

        gameSearchViewModel.results.observe(this) { results ->
            gameListAdapter.updateRepoList(results)
        }

        if (intent != null && intent.hasExtra(EXTRA_DEVELOPER_ID)) {
            developerID = intent.getSerializableExtra(EXTRA_DEVELOPER_ID) as Int
            Log.d(tag, developerID!!.toString())
            developerViewModel.loadResults(developerID!!, RAWG_API_KEY)
        }

        developerViewModel.results.observe(this) { results ->
            if (results != null) {

                findViewById<TextView>(R.id.Developer_Name).text = results.name
                findViewById<TextView>(R.id.developer_games).text = "Games by this Developer:"

                if(results.description.isNullOrEmpty()){
                    findViewById<TextView>(R.id.Developer_Details).text = "There is no description for this developer"
                }else {
                    val desc: TextView = findViewById(R.id.Developer_Details)
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
                val games = results.games
                findViewById<TextView>(R.id.Developer_Game_Count).text = "Games#\n"+ games.toString()

                if (games > 20){
                    gameSearchViewModel.loadResults(RAWG_API_KEY, null, null, null, "20", null, developerID.toString())
                }else if (games <= 2 || games %2 == 0){
                    gameSearchViewModel.loadResults(RAWG_API_KEY, null, null, null, games.toString(), null, developerID.toString())
                }
                else{
                    gameSearchViewModel.loadResults(RAWG_API_KEY, null, null, null, (games - 1).toString(), null, developerID.toString())
                }



            }
        }


        gameSearchViewModel.loading.observe(this){ loading->
            when(loading){
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


    private fun onGameListClick(gameListItem: GameListItem) {
        Log.d(tag, gameListItem.toString())
        val intent = Intent(this, GameDetailActivity::class.java).apply {
            putExtra(EXTRA_GAME_ID,gameListItem.id)
        }
        startActivity(intent)
    }
}