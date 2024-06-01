package com.example.medroute.data.remote.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medroute.domain.model.Route


data class User(
    @PrimaryKey val email: String,
    val userId: Int,
    val name: String,
    val password: String,
    val privilege: String,
    val routes: List<Route>
)
