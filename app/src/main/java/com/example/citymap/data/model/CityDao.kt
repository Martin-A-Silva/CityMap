package com.example.citymap.data.model

import android.os.Parcelable
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


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
}

@Entity(indices = [Index("name")])
@Serializable
@Parcelize
data class City(
    @PrimaryKey @SerializedName("_id") val id: Int,
    val name: String,
    val country: String,
    @Embedded(prefix = "coord_") val coord: Coord,
    val isFavorite: Boolean = false
) : Parcelable

@Serializable
@Parcelize
data class Coord(
    val lon: Double,
    val lat: Double
) : Parcelable
