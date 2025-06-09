package com.example.citymap.di

import android.app.Application
import androidx.room.Room
import com.example.citymap.data.local.AppDatabase
import com.example.citymap.data.local.CityDao
import com.example.citymap.data.local.MetaDataDao
import com.example.citymap.data.remote.CityApi
import com.example.citymap.data.repository.CityRepository
import com.example.citymap.util.Constants.BASE_URL
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
    fun provideCityRepository(api: CityApi, cityDao: CityDao, metaDataDao: MetaDataDao) =
        CityRepository(api, cityDao, metaDataDao)

    @Singleton
    @Provides
    fun provideCityApi(): CityApi {
        return Retrofit.Builder()
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .baseUrl(BASE_URL)
            .build()
            .create(CityApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "cities.db").build()

    @Provides
    fun provideCityDao(db: AppDatabase): CityDao = db.cityDao()

    @Provides
    fun provideMetaDataDao(db: AppDatabase): MetaDataDao = db.metaDataDao()

}