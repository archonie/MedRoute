package com.example.medroute.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.medroute.domain.model.Route
import com.example.medroute.domain.model.UserEntity

@Database(entities = [UserEntity::class, Route::class], version = 15)
abstract class UserDatabase: RoomDatabase() {
    abstract val userDao: UserDao
    abstract val routeDao: RouteDao
}