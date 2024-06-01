package com.example.medroute.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Hotel(
    @PrimaryKey val id: Int,
    val locationId: Int,
    val hotelName: String,
    val starRating: Int
)