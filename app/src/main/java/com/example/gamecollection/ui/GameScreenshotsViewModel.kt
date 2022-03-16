package com.example.gamecollection.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamecollection.api.RAWGService
import com.example.gamecollection.data.GameRepository
import com.example.gamecollection.data.GameScreenshots
import com.example.gamecollection.data.LoadingStatus
import kotlinx.coroutines.launch

class GameScreenshotsViewModel : ViewModel() {
    private val repository = GameRepository(RAWGService.create())

    private val _results = MutableLiveData<GameScreenshots?>(null)
    val results: LiveData<GameScreenshots?> = _results

    private val _loading = MutableLiveData(LoadingStatus.SUCCESS)
    val loading: LiveData<LoadingStatus> = _loading

    private val _error = MutableLiveData<Throwable?>(null)
    val error: LiveData<Throwable?> =_error

    fun loadResults(
        game_pk: String,
        key: String
    ) {
        viewModelScope.launch {
            _loading.value = LoadingStatus.LOADING
            val result = repository.loadGameScreenshots(game_pk, key)
            _results.value = result.getOrNull()
            _loading.value = when (result.isSuccess) {
                true -> LoadingStatus.SUCCESS
                false -> LoadingStatus.ERROR
            }
        }
    }
}