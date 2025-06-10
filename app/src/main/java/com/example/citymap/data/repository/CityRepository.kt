package com.example.citymap.data.repository

import androidx.paging.PagingData
import com.example.citymap.data.model.City
import kotlinx.coroutines.flow.Flow

interface CityRepository {
    suspend fun downloadAndCacheCitiesIfEmpty()
    fun getCities(prefix: String, onlyFavorites: Boolean): Flow<PagingData<City>>
    suspend fun toggleFavorite(cityId: Int, favorite: Boolean)
}