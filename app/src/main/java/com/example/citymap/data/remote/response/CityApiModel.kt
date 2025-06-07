package com.example.citymap.data.remote.response

import com.google.gson.annotations.SerializedName

data class CityApiModel(
    val country: String,
    val name: String,
    @SerializedName("_id")
    val id: Int,
    val coord: CoordApiModel
)
