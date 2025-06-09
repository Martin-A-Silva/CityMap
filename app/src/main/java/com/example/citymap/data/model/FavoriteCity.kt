package com.example.citymap.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteCity(
    @PrimaryKey val cityId: Int
)
