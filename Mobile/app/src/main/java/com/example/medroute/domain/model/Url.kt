package com.example.medroute.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class Url(
    @PrimaryKey val urlId: Int,
    val url: String,
)
