package com.example.medroute.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medroute.data.remote.dto.User
import com.example.medroute.domain.model.UserEntity


@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE userId = :id")
    suspend fun getUser(id: Int): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)
}