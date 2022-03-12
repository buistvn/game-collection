package com.example.gamecollection.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamecollection.api.RAWGService
import com.example.gamecollection.data.GameList
import com.example.gamecollection.data.GameRepository
import kotlinx.coroutines.launch

class GameSearchViewModel : ViewModel() {
    private val repository = GameRepository(RAWGService.create())

    private val _results = MutableLiveData<GameList?>(null)
    val results: LiveData<GameList?> = _results

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<Throwable?>(null)
    val error: LiveData<Throwable?> =_error

    fun loadResults(
        key: String,
        search: String?,
        dates: String?,
        ordering: String?
    ) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.loadGameList(key, search, dates, ordering)
            _loading.value = false
            _error.value = result.exceptionOrNull()
            _results.value = result.getOrNull()
        }
    }
}
