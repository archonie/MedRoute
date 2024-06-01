package com.example.medroute.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Landmark(
    @PrimaryKey val id: Int,
    val locationId: Int,
    val landmarkName: String,
    val landmarkInfo: String
)
