package com.example.citymap.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import com.example.citymap.data.model.FavoriteCity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favoriteCity: FavoriteCity)

    @Delete
    suspend fun removeFavorite(favoriteCity: FavoriteCity)

    @Query("SELECT cityId FROM FavoriteCity")
    fun getFavoriteIds(): Flow<List<Int>>
}
