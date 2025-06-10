package com.example.citymap.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.citymap.data.local.CityDao
import com.example.citymap.data.local.MetaDataDao
import com.example.citymap.data.model.City
import com.example.citymap.data.model.MetaData
import com.example.citymap.data.remote.CityApi
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okio.IOException
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityRepositoryImpl @Inject constructor(
    private val api: CityApi,
    private val cityDao: CityDao,
    private val metaDataDao: MetaDataDao
) : CityRepository {
    override suspend fun downloadAndCacheCitiesIfEmpty() = withContext(Dispatchers.IO) {
        val isCompleted = metaDataDao.getValue("download_completed")?.toBoolean() ?: false
        if (isCompleted) return@withContext

        val response = api.getCityList()
        if (!response.isSuccessful || response.body() == null) {
            throw IOException("Download failed: ${response.code()}")
        }

        val gson = Gson()

        // Using JsonReader and InputStreamReader to more efficiently read such a large JSON array
        val reader = JsonReader(InputStreamReader(response.body()!!.byteStream()))
        val batch = mutableListOf<City>()

        try {
            reader.beginArray()
            while (reader.hasNext()) {
                val city: City = gson.fromJson(reader, City::class.java)
                batch.add(city)

                /*
                 * Storing the cities in a local database will provide:
                 * 1- Faster data access and filtering since it can be indexed by name
                 * 2- Better loading times as the data is already cached
                 * 3- Save favorite state for each city
                 */
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

    override fun getCities(prefix: String, onlyFavorites: Boolean): Flow<PagingData<City>> {
        return Pager(PagingConfig(pageSize = 50)) {
            cityDao.searchCities(prefix, onlyFavorites)
        }.flow
    }

    override suspend fun toggleFavorite(cityId: Int, favorite: Boolean) {
        cityDao.setFavorite(cityId, favorite)
    }
}
