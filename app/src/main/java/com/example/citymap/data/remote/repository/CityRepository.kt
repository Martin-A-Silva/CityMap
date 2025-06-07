package com.example.citymap.data.remote.repository

import com.example.citymap.data.remote.CityApi
import com.example.citymap.data.remote.response.CityApiModel
import com.example.citymap.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class CityRepository @Inject constructor(private val api: CityApi) {

    suspend fun getCityList(): Resource<List<CityApiModel>> {
        val response = try {
            api.getCityList()
        } catch (e: Exception) {
            return Resource.Error(null, "unknown error ocurred")
        }
        return Resource.Success(response)
    }
}