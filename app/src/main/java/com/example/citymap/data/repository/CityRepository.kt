package com.example.citymap.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.citymap.data.model.City
import com.example.citymap.data.local.CityDao
import com.example.citymap.data.local.MetaDataDao
import com.example.citymap.data.model.MetaData
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
class CityRepository @Inject constructor(
    private val api: CityApi,
    private val cityDao: CityDao,
    private val metaDataDao: MetaDataDao
) {
    suspend fun downloadAndCacheCitiesIfEmpty() = withContext(Dispatchers.IO) {
        val isCompleted = metaDataDao.getValue("download_completed")?.toBoolean() ?: false
        if (isCompleted) return@withContext

        val response = api.getCityList()
        if (!response.isSuccessful || response.body() == null) {
            throw IOException("Download failed: ${response.code()}")
        }

        val gson = Gson()
        val reader = JsonReader(InputStreamReader(response.body()!!.byteStream()))
        val batch = mutableListOf<City>()

        try {
            reader.beginArray()
            while (reader.hasNext()) {
                val city: City = gson.fromJson(reader, City::class.java)
                batch.add(city)

                if (batch.size >= 1000) {
                    cityDao.insertCities(batch)
                    batch.clear()
                }
            }
            if (batch.isNotEmpty()) {
                cityDao.insertCities(batch)
            }

            reader.endArray()

            metaDataDao.setValue(MetaData("download_completed", "true"))

        } catch (e: Exception) {
            throw IOException("Failed to complete city download: ${e.message}", e)
        } finally {
            reader.close()
            response.body()!!.close()
        }
    }

    fun getCities(prefix: String, onlyFavorites: Boolean): Flow<PagingData<City>> {
        return Pager(PagingConfig(pageSize = 50)) {
            cityDao.searchCities(prefix, onlyFavorites)
        }.flow
    }

    suspend fun toggleFavorite(cityId: Int, favorite: Boolean) {
        cityDao.setFavorite(cityId, favorite)
    }
}
