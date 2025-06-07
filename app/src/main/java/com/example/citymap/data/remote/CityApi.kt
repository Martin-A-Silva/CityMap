package com.example.citymap.data.remote

import com.example.citymap.data.remote.response.CityApiModel
import retrofit2.http.GET

interface CityApi {

    @GET
    suspend fun getCityList(): List<CityApiModel>
}