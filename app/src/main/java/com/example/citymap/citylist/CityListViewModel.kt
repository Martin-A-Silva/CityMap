package com.example.citymap.citylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.citymap.data.model.City
import com.example.citymap.data.repository.CityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityListViewModel @Inject constructor(
    private val repository: CityRepository
) : ViewModel() {

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    private val searchPrefix = MutableStateFlow("")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val cityPagingData = combine(searchPrefix, showFavoritesOnly) { prefix, onlyFavs ->
        repository.getCities(prefix, onlyFavs)
    }.flatMapLatest { it }
        .cachedIn(viewModelScope)

    init {
        loadCitiesFromNetwork()
    }

    fun updatePrefix(prefix: String) {
        searchPrefix.value = prefix
    }

    fun toggleFavoritesOnly() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }

    fun toggleFavorite(city: City) {
        viewModelScope.launch {
            repository.toggleFavorite(city.id, !city.isFavorite)
        }
    }

    private fun loadCitiesFromNetwork() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.downloadAndCacheCitiesIfEmpty()
            _isLoading.value = false
        }
    }
}
