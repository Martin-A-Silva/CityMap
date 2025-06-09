package com.example.citymap.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Coord(
    val lon: Double,
    val lat: Double
) : Parcelable