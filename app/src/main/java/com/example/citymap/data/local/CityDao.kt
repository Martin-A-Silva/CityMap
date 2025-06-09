package com.example.citymap.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.citymap.data.model.City


@Dao
interface CityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cities: List<City>)

    @Query("SELECT * FROM City WHERE name LIKE :prefix || '%' ORDER BY name")
    fun searchCitiesByPrefix(prefix: String): PagingSource<Int, City>

    @Query("SELECT * FROM City WHERE id IN (:ids) AND name LIKE :prefix || '%' ORDER BY name")
    fun searchCitiesByIdsAndPrefix(ids: List<Int>, prefix: String): PagingSource<Int, City>

    @Query("SELECT * FROM City WHERE name LIKE :prefix || '%' ORDER BY name")
    suspend fun searchCitiesBlocking(prefix: String): List<City>

}


