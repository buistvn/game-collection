package com.example.gamecollection.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamecollection.api.RAWGService
import com.example.gamecollection.data.GameList
import com.example.gamecollection.data.GameRepository
import com.example.gamecollection.data.LoadingStatus
import kotlinx.coroutines.launch

class GameSearchViewModel : ViewModel() {
    private val repository = GameRepository(RAWGService.create())

    private val _results = MutableLiveData<GameList?>(null)
    val results: LiveData<GameList?> = _results

    private val _loading = MutableLiveData(LoadingStatus.SUCCESS)
    val loading: LiveData<LoadingStatus> = _loading

    private val _error = MutableLiveData<Throwable?>(null)
    val error: LiveData<Throwable?> = _error

    fun loadResults(
        key: String,
        search: String?,
        dates: String?,
        ordering: String?,
        page_size: String?,
        genres: String?
    ) {
        viewModelScope.launch {
            _loading.value = LoadingStatus.LOADING
            val result = repository.loadGameList(key, search, dates, ordering, page_size, genres)
            _results.value = result.getOrNull()
            _loading.value = when (result.isSuccess) {
                true -> LoadingStatus.SUCCESS
                false -> LoadingStatus.ERROR
            }
        }
    }
}
