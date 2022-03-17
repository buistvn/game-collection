package com.example.gamecollection.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamecollection.R
import com.example.gamecollection.data.GameListItem
import com.example.gamecollection.data.LoadingStatus
import com.example.gamecollection.data.Store
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.io.IOException

const val EXTRA_GAME_LIST_ITEM = "com.example.gamecollection.GAME_LIST_ITEM"

class GameDetailActivity : AppCompatActivity(), TextureView.SurfaceTextureListener, MediaController.MediaPlayerControl {
    private val tag = "GameDetailActivity"
    private var gameListItem: GameListItem? = null
    private var isFavorite = false

    private val gameDetailsViewModel: GameDetailsViewModel by viewModels()
    private val gameSearchViewModel: GameSearchViewModel by viewModels()
    private val gameScreenshotsViewModel: GameScreenshotsViewModel by viewModels()
    private val gameTrailerViewModel: GameTrailerViewModel by viewModels()
    private val favoriteGamesViewModel: FavoriteGamesViewModel by viewModels()

    private lateinit var gameListAdapter: GameListAdapter
    private lateinit var storeAdapter: StoresAdapter

    private lateinit var detailsLayout: LinearLayout
    private lateinit var searchErrorTV: TextView
    private lateinit var loadingIndicator: CircularProgressIndicator
    private lateinit var searchResultListRV: RecyclerView
    private lateinit var storeListRV: RecyclerView
    private lateinit var favoriteButton: MaterialButton

    private lateinit var textureView: TextureView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var url: String
    private lateinit var mediaController: MediaController
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_details)
        scrollView = findViewById(R.id.scroll_view)

        detailsLayout = findViewById(R.id.details)
        searchErrorTV = findViewById(R.id.tv_search_error)
        loadingIndicator = findViewById(R.id.loading_indicator)
        searchResultListRV = findViewById(R.id.rv_search_results)
        storeListRV = findViewById(R.id.rv_stores)
        favoriteButton = findViewById(R.id.bt_favorite)

        gameListAdapter = GameListAdapter(::onGameListClick)
        storeAdapter = StoresAdapter(::onStoreClick)
        searchResultListRV.layoutManager = GridLayoutManager(this,2)
        searchResultListRV.setHasFixedSize(true)

        storeListRV.layoutManager = LinearLayoutManager(this)
        storeListRV.setHasFixedSize(true)

        searchResultListRV.adapter = gameListAdapter
        storeListRV.adapter = storeAdapter

        gameSearchViewModel.results.observe(this) { results ->
            gameListAdapter.updateGameListItems(results?.results)
        }

        if (intent != null && intent.hasExtra(EXTRA_GAME_LIST_ITEM)) {
            gameListItem = intent.getSerializableExtra(EXTRA_GAME_LIST_ITEM) as GameListItem
            Log.d(tag, gameListItem!!.toString())
            gameDetailsViewModel.loadResults(gameListItem!!.id, RAWG_API_KEY)
            gameScreenshotsViewModel.loadResults(gameListItem!!.id.toString(), RAWG_API_KEY)
            gameTrailerViewModel.loadResults(gameListItem!!.id, RAWG_API_KEY)
        }

        gameDetailsViewModel.results.observe(this) { results ->
            if (results != null) {
                // genres
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
                findViewById<TextView>(R.id.tv_genres).text = genres

                // title
                findViewById<TextView>(R.id.tv_game_title).text = results.name

                // rating
                val rating = "Rating: " + results.rating.toString() + "/5"
                findViewById<TextView>(R.id.tv_rating).text = rating

                // release date
                val releasedDate = "Released: " + results.released
                findViewById<TextView>(R.id.tv_released).text = releasedDate

                // tags
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

                // description
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

                // stores
                storeAdapter.updateStoreList(results.stores)
                val stores: TextView = findViewById(R.id.tv_stores)

                val noStores = "Not found in stores"
                val yesStores = "Stores:"
                if (results.stores.isNullOrEmpty()) stores.text = noStores
                else stores.text = yesStores

                // screenshots
                gameScreenshotsViewModel.results.observe(this) { ssResults ->
                    val screenshots: LinearLayout = findViewById(R.id.screenshots)
                    if (ssResults != null) {
                        Log.d(tag, ssResults.toString())
                        for (screenshot in ssResults.results) {
                            val tempSS = ImageView(this)
                            tempSS.layoutParams = LinearLayout.LayoutParams(
                                800,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            Glide.with(this)
                                .load(screenshot.image)
                                .into(tempSS)
                            screenshots.addView(tempSS)
                        }
                    }
                }

                // trailer
                gameTrailerViewModel.results.observe(this) { trailerResults ->
                    if (trailerResults != null) {
                        var trailerTitle = ""
                        if (trailerResults.count > 0) {
                            url = trailerResults.results[0].data.normal
                            if (!url.startsWith("http://") && !url.startsWith("https://"))
                                url = "http://$url"

                            mediaPlayer = MediaPlayer().apply {
                                setAudioAttributes(
                                    AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                        .build()
                                )
                                setDataSource(url)
                                prepare() // might take long! (for buffering, etc)
                            }
                            textureView = findViewById(R.id.tv_trailer)
                            textureView.surfaceTextureListener = this
                            textureView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 600)

                            mediaController = MediaController(this)

                            trailerTitle = "Trailer:"
                        } else {
                            trailerTitle = "No Trailer"
                        }
                        findViewById<TextView>(R.id.tv_trailer_title).text = trailerTitle
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

        favoriteGamesViewModel.getFavoriteGameById(gameListItem!!.id).observe(this) { favoriteGame ->
            when (favoriteGame) {
                null -> {
                    isFavorite = false
                    favoriteButton.setText(R.string.favorite_off)
                    favoriteButton.setIconResource(R.drawable.ic_button_favorite_off)
                }
                else -> {
                    isFavorite = true
                    favoriteButton.setText(R.string.favorite_on)
                    favoriteButton.setIconResource(R.drawable.ic_button_favorite_on)
                }
            }
        }

        favoriteButton.setOnClickListener {
            if (gameListItem != null) {
                isFavorite = !isFavorite
                when (isFavorite) {
                    true -> {
                        favoriteGamesViewModel.addFavoriteGame(gameListItem!!)
                    }
                    false -> {
                        favoriteGamesViewModel.removeFavoriteGame(gameListItem!!)
                    }
                }
            }
        }
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        val surface = Surface(surfaceTexture)
        Log.d("hihihi", url)
        mediaController.setAnchorView(textureView)
        try {
            mediaController.setMediaPlayer(this)
            mediaPlayer.setSurface(surface)
            mediaPlayer.setOnPreparedListener {
                start()
                mediaController.isEnabled
                pause()
                mediaController.show()
            }
        } catch (e: IOException) { e.printStackTrace() }
        textureView.setOnClickListener { mediaController.show() }
        scrollView.viewTreeObserver.addOnScrollChangedListener { mediaController.hide() }
    }
    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return false
    }
    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

    private fun onGameListClick(gameListItem: GameListItem) {
        Log.d(tag, gameListItem.toString())
        val intent = Intent(this, GameDetailActivity::class.java).apply {
            putExtra(EXTRA_GAME_LIST_ITEM, gameListItem)
        }
        startActivity(intent)
    }

    private fun onStoreClick(store: Store) {
        Log.d(tag, store.toString())
        var url = store.store.domain
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://$url"
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(webIntent)
        } catch (e: ActivityNotFoundException) {
        }
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun getDuration(): Int {
        return mediaPlayer.duration
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun seekTo(p0: Int) {
        mediaPlayer.seekTo(p0)
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun getBufferPercentage(): Int {
        return 0
    }

    override fun canPause(): Boolean {
        return true
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun getAudioSessionId(): Int {
        return mediaPlayer.audioSessionId
    }
}
