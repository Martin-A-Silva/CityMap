package com.example.citymap.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MetaData(
    @PrimaryKey val key: String,
    val value: String
)
