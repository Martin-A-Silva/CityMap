package com.example.citymap.data.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.citymap.data.model.City
import com.example.citymap.data.local.CityDao
import com.example.citymap.data.model.FavoriteCity
import com.example.citymap.data.local.FavoriteCityDao
import com.example.citymap.data.paging.FavoriteCityPagingSource
import com.example.citymap.data.remote.CityApi
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okio.IOException
import java.io.InputStreamReader
import javax.inject.Inject

@ActivityScoped
class CityRemoteRepository @Inject constructor(
    private val api: CityApi,
    private val cityDao: CityDao,
    private val favoriteCityDao: FavoriteCityDao
) {
    suspend fun downloadAndCacheCities() = withContext(Dispatchers.IO) {
        val response = api.getCityList()
        if (!response.isSuccessful || response.body() == null) {
            throw IOException("Download failed: ${response.code()}")
        }

        val gson = Gson()
        val reader = JsonReader(InputStreamReader(response.body()!!.byteStream()))
        val batch = mutableListOf<City>()

        reader.beginArray()
        while (reader.hasNext()) {
            val city : City = gson.fromJson(reader, City::class.java)
            batch.add(city)
            if (batch.size >= 1000) {
                cityDao.insertCities(batch)
                batch.clear()
            }
        }
        reader.endArray()

        if (batch.isNotEmpty()) {
            cityDao.insertCities(batch)
        }

        reader.close()
        response.body()!!.close()
    }

    fun getFavoriteIds(): Flow<List<Int>> = favoriteCityDao.getFavoriteIds()

    fun getCities(prefix: String, onlyFavorites: Boolean): Flow<PagingData<City>> {
        return Pager(PagingConfig(pageSize = 50)) {
            if (onlyFavorites) {
                FavoriteCityPagingSource(cityDao, favoriteCityDao, prefix)
            } else {
                cityDao.searchCitiesByPrefix(prefix)
            }
        }.flow
    }

    suspend fun toggleFavorite(cityId: Int, shouldFavorite: Boolean) {
        if (shouldFavorite) {
            favoriteCityDao.addFavorite(FavoriteCity(cityId))
        } else {
            favoriteCityDao.removeFavorite(FavoriteCity(cityId))
        }
    }
}
