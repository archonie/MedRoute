package com.example.medroute.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class Route(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val mail: String ,
    val hotelId: Int,
    val hospitalId: Int,
    val type: String,
    val routeName: String
)