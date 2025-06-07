package com.example.citymap.di

import com.example.citymap.data.remote.CityApi
import com.example.citymap.data.remote.repository.CityRepository
import com.example.citymap.util.Constants.BASE_URL
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideCityRepository(api: CityApi) = CityRepository(api)

    @Singleton
    @Provides
    fun provideCityApi(): CityApi {
        return Retrofit.Builder()
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
                )
            )
            .baseUrl(BASE_URL)
            .build()
            .create(CityApi::class.java)
    }
}