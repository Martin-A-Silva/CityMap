package com.example.citymap.data.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.citymap.data.model.City
import com.example.citymap.data.model.CityDao
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
    private val dao: CityDao
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
            //val city = gson.fromJson(reader, City::class.java)
            val city : City = gson.fromJson(reader, City::class.java)
            batch.add(city)
            if (batch.size >= 1000) {
                dao.insertCities(batch)
                batch.clear()
            }
        }
        reader.endArray()

        if (batch.isNotEmpty()) {
            dao.insertCities(batch)
        }

        reader.close()
        response.body()!!.close()
    }

    fun getCitiesByPrefix(prefix: String): Flow<PagingData<City>> {
        return Pager(PagingConfig(pageSize = 50)) {
            dao.searchCitiesByPrefix(prefix)
        }.flow
    }
}
