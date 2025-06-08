package com.example.citymap.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class CityApiModel(
    val country: String,
    val name: String,
    @SerializedName("_id")
    val id: Int,
    val coord: CoordApiModel
) : Parcelable
