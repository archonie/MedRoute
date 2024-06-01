package com.example.medroute.domain.model

import androidx.room.PrimaryKey


data class Hospital(
    @PrimaryKey val id: Int,
    val locationId: Int,
    val hospitalName: String,
    val hospitalType: String
)