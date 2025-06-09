package com.example.citymap.data.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

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