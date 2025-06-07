package com.example.citymap.data.remote

import com.example.citymap.data.remote.response.CityApiModel
import retrofit2.http.GET

interface CityApi {

    @GET("cities.json")
    suspend fun getCityList(): List<CityApiModel>
}