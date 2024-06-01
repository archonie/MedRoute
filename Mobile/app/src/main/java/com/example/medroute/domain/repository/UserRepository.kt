package com.example.medroute.domain.repository

import com.example.medroute.domain.model.Hospital
import com.example.medroute.domain.model.Hotel
import com.example.medroute.domain.model.Landmark
import com.example.medroute.domain.model.Location
import com.example.medroute.domain.model.Route
import com.example.medroute.domain.model.UserEntity

interface UserRepository {

    suspend fun login(email: String, password: String): Result<String>
    suspend fun getUser(): UserEntity?
    suspend fun deleteAllRoutes()
    suspend fun getRoutes(): List<Route>
    suspend fun getHotel(id: Int): Result<Hotel>

    suspend fun getHospital(id: Int): Result<Hospital>
     //suspend fun getRoutes(): List<Route>
//
    suspend fun insertRoute(route: Route)
//    suspend fun deleteRoute(route: RouteEntity)

    suspend fun signUp(mail: String, password: String, confirmPassword: String, name: String): Result<Boolean>
    suspend fun getLandmark(id: Int): Result<Landmark>
    suspend fun searchHotels(query: String): Result<List<Hotel>>
    suspend fun searchHospitals(query: String): Result<List<Hospital>>

    suspend fun getRoute(id: Int): Route?

    suspend fun getLocation(id: Int): Result<Location>
    suspend fun deleteUser(user: UserEntity)
    suspend fun postRoute(mail: String, hotelId: Int, hospitalId: Int, type: String, routeName: String): Result<Int>

    suspend fun getAllRoutes(mail: String): Result<List<Route>>

    suspend fun deleteRoute(id:Int): Result<Boolean>

    suspend fun searchLandmarks(latitude: Double, longitude: Double, radius: Double, requestedNumber: Int): Result<List<Landmark>>
}