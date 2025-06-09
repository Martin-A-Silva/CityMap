package com.example.citymap.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.citymap.data.model.City
import com.example.citymap.data.model.MetaData

@Database(entities = [City::class, MetaData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun metaDataDao(): MetaDataDao

}