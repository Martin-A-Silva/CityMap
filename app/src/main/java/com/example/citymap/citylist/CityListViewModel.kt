package com.example.citymap.citylist

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citymap.data.remote.repository.CityRepository
import com.example.citymap.data.remote.response.CityApiModel
import com.example.citymap.util.Trie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CityListViewModel @Inject constructor(
    private val repository: CityRepository
) : ViewModel() {

    var isLoading = mutableStateOf(false)
    var loadError = mutableStateOf("")
    var isSearching = mutableStateOf(false)
    var loaded = false //TODO: remove

    private var citiesTrie = Trie()
    var cities = mutableStateOf(emptyList<CityApiModel>())

    init {
        getCityList()
    }

    private fun getCityList() {
        if (loaded) return
        isLoading.value = true
        viewModelScope.launch {
            runCatching {
                repository.getCityList()
            }.onSuccess { response ->
                cities.value = response.data!!.sortedBy { it.name }
                withContext(Dispatchers.Default) {
                    parseToTrie(cities.value)
                }
                isLoading.value = false
                loaded = true
            }.onFailure { error ->

            }
        }
    }

    private fun parseToTrie(cities: List<CityApiModel>) {
        for (city in cities) {
            citiesTrie.insert(city.name)
        }
    }

    fun filterCitiesByPrefix(prefix: String): List<CityApiModel> {
        val matchingNames = citiesTrie.searchByPrefix(prefix)
        return matchingNames.mapNotNull { name ->
            cities.value.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}