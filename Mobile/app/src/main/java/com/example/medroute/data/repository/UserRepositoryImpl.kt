package com.example.medroute.data.repository

import android.util.Log
import com.example.medroute.data.local.RouteDao
import com.example.medroute.data.local.UserDao
import com.example.medroute.data.remote.ApiService
import com.example.medroute.data.remote.LoginRequest
import com.example.medroute.data.remote.RouteRequest
import com.example.medroute.data.remote.SignUpRequest
import com.example.medroute.domain.model.Hospital
import com.example.medroute.domain.model.Hotel
import com.example.medroute.domain.model.Landmark
import com.example.medroute.domain.model.Location
import com.example.medroute.domain.model.Route
import com.example.medroute.domain.model.UserEntity
import com.example.medroute.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val routeDao: RouteDao
) : UserRepository {
    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                response.body()?.let {
                    userDao.insert(UserEntity(userId = 1, email = email, token = it.token))
                    Result.success(it.token)
                } ?: Result.failure(Exception("Invalid response."))
            } else {
                Result.failure(Exception("Login failed."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getAllRoutes(mail: String): Result<List<Route>> {
        return try {
            val response = apiService.getRoutes(mail)
            Log.d("RouteScreen", "$response")
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    val routes = apiResponse.data
                    if (routes.isNotEmpty()) {
                        routes.forEach { route ->
                            routeDao.insertRoute(route)
                        }
                        Result.success(routes)
                    } else {
                        Result.failure(Exception("No routes found in the response."))
                    }
                } ?: Result.failure(Exception("Response body is null."))
            } else {
                Result.failure(Exception("Failed to fetch routes: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteRoute(id: Int): Result<Boolean> {
        return try {
            val response = apiService.deleteRoute(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    routeDao.deleteRoute(id)
                    Result.success(it.succeeded)
                } ?: Result.failure(Exception("Invalid response. Body is null."))
            } else {
                Result.failure(Exception("Delete route failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun signUp(
        mail: String,
        password: String,
        confirmPassword: String,
        name: String
    ): Result<Boolean> {
        return try {
            val response = apiService.signUp(
                SignUpRequest(
                    name = name,
                    password = password,
                    confirmPassword = confirmPassword,
                    mail = mail
                )
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.succeeded)
                } ?: Result.failure(Exception("Invalid response."))
            } else {
                Result.failure(Exception("Sign up failed."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun postRoute(
        mail: String,
        hotelId: Int,
        hospitalId: Int,
        type: String,
        routeName: String
    ): Result<Int> {
        return try {
            val response = apiService.postRoute(
                RouteRequest(
                    mail = mail,
                    hotelId = hotelId,
                    hospitalId = hospitalId,
                    type = type,
                    routeName = routeName
                )
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.data)
                } ?: Result.failure(Exception("Invalid response. Body is null."))
            } else {
                val errorBody = response.errorBody()?.string()

                Result.failure(Exception("Post route failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHotel(id: Int): Result<Hotel> {
        return try {
            val response = apiService.getHotel(id)
            if(response.isSuccessful){
                response.body()?.let {
                    Result.success(it.data)
                } ?: Result.failure(Exception("Invalid response."))
            }else{
                Result.failure(Exception("Hotel not found."))
            }
        }catch (e:Exception){
            Result.failure(e)
        }
    }
    override suspend fun getLandmark(id: Int): Result<Landmark> {
        return try{
            val response = apiService.getLandmark(id)
            if(response.isSuccessful){
                response.body()?.let {
                    Result.success(it.data)
                } ?: Result.failure(Exception("Invalid response."))
            }else{
                Result.failure(Exception("Landmark not found."))
            }
        }catch (e:Exception){
            Result.failure(e)
        }
    }


    override suspend fun getHospital(id: Int): Result<Hospital> {
        return try {
            val response = apiService.getHospital(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.data)
                } ?: Result.failure(Exception("Invalid response."))
            } else {
                Result.failure(Exception("Hospital not found."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun searchHotels(query: String): Result<List<Hotel>> {
        return try {
            val response = apiService.getHotels(query = query, requestedNumber = 10)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.data)
                } ?: Result.failure(Exception("Invalid response."))
            } else {
                Result.failure(Exception("Hospital not found."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchLandmarks(
        latitude: Double,
        longitude: Double,
        radius: Double,
        requestedNumber: Int
    ): Result<List<Landmark>> {
        return try {
            val response = apiService.getLandmarks(
                latitude = latitude,
                longitude = longitude,
                radius = radius,
                requestedNumber = requestedNumber
            )
            if (response.isSuccessful) {
                Log.d("landmarks", "success")
                response.body()?.let {
                    Log.d("landmarks", "${it.data}")
                    Result.success(it.data)
                } ?: Result.failure(Exception("Invalid response."))
            } else {
                Result.failure(Exception("Landmark not found."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun searchHospitals(query: String): Result<List<Hospital>> {
        return try {
            val response = apiService.getHospitals(query = query, 10)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.data)
                } ?: Result.failure(Exception("Invalid response."))
            } else {
                Result.failure(Exception("Hospital not found."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getRoute(id: Int): Route {
        return routeDao.getRoute(id)
    }

    override suspend fun getLocation(id: Int): Result<Location> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getLocation(id)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it.data)
                    } ?: Result.failure(Exception("Invalid response."))
                } else {
                    Result.failure(Exception("Location not found."))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun deleteUser(user: UserEntity){
        userDao.delete(user)
        deleteAllRoutes()
    }
    override suspend fun getUser(): UserEntity? {
        return userDao.getUser(1)
    }

    override suspend fun deleteAllRoutes() {
        routeDao.deleteAllRoutes()
    }

    override suspend fun getRoutes(): List<Route> {
        return routeDao.getRoutes()
    }


    override suspend fun insertRoute(route: Route) {
        routeDao.insertRoute(route)
    }


}