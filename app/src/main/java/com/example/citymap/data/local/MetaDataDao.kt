package com.example.citymap.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.citymap.data.model.MetaData

@Dao
interface MetaDataDao {
    @Query("SELECT value FROM MetaData WHERE key = :key")
    suspend fun getValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setValue(metaData: MetaData)
}