package com.example.gamecollection.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamecollection.api.RAWGService
import com.example.gamecollection.data.DeveloperDetails
import com.example.gamecollection.data.GameDetails
import com.example.gamecollection.data.GameRepository
import com.example.gamecollection.data.LoadingStatus
import kotlinx.coroutines.launch

class DeveloperDetailsViewModel: ViewModel() {
    private val repository = GameRepository(RAWGService.create())

    private val _results = MutableLiveData<DeveloperDetails?>(null)
    val results: LiveData<DeveloperDetails?> = _results

    private val _loading = MutableLiveData(LoadingStatus.SUCCESS)
    val loading: LiveData<LoadingStatus> = _loading

    private val _error = MutableLiveData<Throwable?>(null)
    val error: LiveData<Throwable?> =_error

    fun loadResults(
        id: Int,
        key: String
    ) {
        viewModelScope.launch {
            _loading.value = LoadingStatus.LOADING
            val result = repository.loadDeveloperDetails(id, key)
            _results.value = result.getOrNull()
            _loading.value = when (result.isSuccess) {
                true -> LoadingStatus.SUCCESS
                false -> LoadingStatus.ERROR
            }
        }
    }
}