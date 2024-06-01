package com.example.medroute.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("users")
data class UserEntity(
    @PrimaryKey val email: String,
    val userId: Int,
    val token: String?
)
