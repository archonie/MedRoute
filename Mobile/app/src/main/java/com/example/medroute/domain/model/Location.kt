package com.example.medroute.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Location(
    @PrimaryKey val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val url: String
)
