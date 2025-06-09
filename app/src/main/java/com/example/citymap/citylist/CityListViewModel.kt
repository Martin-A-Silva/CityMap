package com.example.citymap.citylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.citymap.data.model.City
import com.example.citymap.data.remote.repository.CityRemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


@HiltViewModel
class CityListViewModel @Inject constructor(
    private val repository: CityRemoteRepository
) : ViewModel() {

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    private val searchPrefix = MutableStateFlow("")

    val favoriteIds: StateFlow<Set<Int>> = repository.getFavoriteIds()
        .map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val cityPagingData =
        combine(searchPrefix, showFavoritesOnly, favoriteIds) { prefix, onlyFavs, favIds ->
            repository.getCities(prefix, onlyFavs)
        }.flatMapLatest { it }
            .cachedIn(viewModelScope)

    fun updatePrefix(prefix: String) {
        searchPrefix.value = prefix
    }

    fun toggleFavoritesOnly() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }

    fun toggleFavorite(city: City) {
        viewModelScope.launch {
            val isCurrentlyFavorite = favoriteIds.value.contains(city.id)
            repository.toggleFavorite(city.id, !isCurrentlyFavorite)
        }
    }

    fun loadCitiesFromNetwork() {
        viewModelScope.launch {
            repository.downloadAndCacheCities()
        }
    }
}
