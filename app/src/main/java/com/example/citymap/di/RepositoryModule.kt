package com.example.citymap.di

import com.example.citymap.data.repository.CityRepository
import com.example.citymap.data.repository.CityRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCityRepository(
        impl: CityRepositoryImpl
    ): CityRepository
}