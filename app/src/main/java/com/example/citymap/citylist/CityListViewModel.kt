package com.example.citymap.citylist

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.citymap.data.remote.repository.CityRemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityListViewModel @Inject constructor(
    private val repository: CityRemoteRepository
) : ViewModel() {

    private val _prefix = MutableStateFlow("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val cityPagingData = _prefix
        .debounce(300)
        .flatMapLatest { repository.getCitiesByPrefix(it) }
        .cachedIn(viewModelScope)

    fun updatePrefix(newPrefix: String) {
        _prefix.value = newPrefix
    }

    fun loadCitiesFromNetwork() {
        viewModelScope.launch {
            try {
                repository.downloadAndCacheCities()
            } catch (e: Exception) {
                Log.e("CityViewModel", "City download failed", e)
            }
        }
    }

    var isLoading = mutableStateOf(false)
    var loadError = mutableStateOf("")
    var isSearching = mutableStateOf(false)
    var loaded = false //TODO: remove


}
