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

    @Query("""
        SELECT * FROM City 
        WHERE name LIKE :prefix || '%' 
        AND (:onlyFavorites IS 0 OR isFavorite = 1)
        ORDER BY name
    """)
    fun searchCities(prefix: String, onlyFavorites: Boolean): PagingSource<Int, City>

    @Query("UPDATE City SET isFavorite = :favorite WHERE id = :cityId")
    suspend fun setFavorite(cityId: Int, favorite: Boolean)

    @Query("SELECT COUNT(*) FROM City")
    suspend fun getCityCount(): Int
}

