package com.example.citymap.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class CoordApiModel (
    val lon: Double,
    val lat: Double
) : Parcelable
