package com.example.gamecollection.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import com.example.gamecollection.R

const val RAWG_API_KEY = "e1dd3dd1ae1b47a49ae5b110b5447c6c"

class MainActivity : AppCompatActivity() {
    private val tag = "MainActivity"

    private val gameSearchViewModel: GameSearchViewModel by viewModels()

    private lateinit var searchInputET: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchInputET = findViewById(R.id.et_search_input)

        /*
        gameSearchViewModel.loadResults.observe(this) { results ->
            // Update RecyclerView adapter
        }
        */

        val searchButton: Button = findViewById(R.id.bt_search)
        searchButton.setOnClickListener {
            val searchQuery = searchInputET.text.toString()

            if (!TextUtils.isEmpty(searchQuery)) {
                // Results on search are from the user's input
                gameSearchViewModel.loadResults(RAWG_API_KEY, searchQuery, null, null)
            }
        }

        // Results on start are the most popular games in 2021
        gameSearchViewModel.loadResults(RAWG_API_KEY, null, "2021-01-01,2021-12-31", "-added")
    }
}
