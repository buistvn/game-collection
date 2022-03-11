package com.example.gamecollection.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gamecollection.R

const val RAWG_API_KEY = "e1dd3dd1ae1b47a49ae5b110b5447c6c"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
