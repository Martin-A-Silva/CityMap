package com.example.citymap.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming

interface CityApi {

    @GET("cities.json")
    @Streaming
    suspend fun getCityList(): Response<ResponseBody>
}