package com.example.medroute.data.remote

import com.example.medroute.domain.model.Hospital
import com.example.medroute.domain.model.Hotel
import com.example.medroute.domain.model.Landmark
import com.example.medroute.domain.model.Location
import com.example.medroute.domain.model.Route
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @Headers("Accept: */*", "Content-Type: application/json")
    @POST("Account/authenticate")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @Headers("Accept: */*", "Content-Type: application/json")
    @POST("Account/register")
    suspend fun signUp(@Body signupRequest: SignUpRequest): Response<SignUpResponse>

    @Headers("Accept: */*", "ContentType: application/json")
    @POST("Route")
    suspend fun postRoute(@Body routeRequest: RouteRequest): Response<RouteResponse>

    @Headers("Accept: text/plain")
    @GET("Route")
    suspend fun getRoutes(@Query("mail") mail: String): Response<RouteListResponse>

    @Headers("Accept: text/plain")
    @GET("Route")
    suspend fun getRoute(@Query("mail") mail: String): Response<RouteListResponse>

    @Headers("Accept: text/plain", )
    @GET("Hotel/filter-name")
    suspend fun getHotels(
        @Query("filter") query: String,
        @Query("RequestedNumber") requestedNumber: Int
    ): Response<HotelSearchResponse>

    @Headers("Accept: text/plain", )
    @GET("Hospital/filter-name")
    suspend fun getHospitals(
        @Query("filter") query: String,
        @Query("RequestedNumber") requestedNumber: Int
    ): Response<HospitalSearchResponse>


    @Headers("Accept: text/plain")
    @GET("Landmark/filter-location")
    suspend fun getLandmarks(
        @Query("CenterLatitude") latitude: Double,
        @Query("CenterLongitude") longitude: Double,
        @Query("Radius") radius: Double,
        @Query("RequestedNumber") requestedNumber: Int
    ): Response<LandmarkSearchResponse>

    @Headers("Accept: */*")
    @GET("Location/{id}")
    suspend fun getLocation(@Path("id") id: Int): Response<LocationResponse>

    @Headers("Accept: */*")
    @DELETE("Route/{id}")
    suspend fun deleteRoute(@Path("id") id: Int): Response<RouteResponse>

    @Headers("Accept: text/plain")
    @GET("Hotel/{id}")
    suspend fun getHotel(@Path("id") id: Int): Response<GetHotel>

    @Headers("Accept: text/plain")
    @GET("Hospital/{id}")
    suspend fun getHospital(@Path("id") id: Int): Response<GetHospital>

    @Headers("Accept: text/plain")
    @GET("Landmark/{id}")
    suspend fun getLandmark(@Path("id") id: Int): Response<GetLandmark>

}


data class LandmarkSearchResponse(
    val succeeded: Boolean,
    val message: String?,
    val errors: Any?,
    val data: List<Landmark>
)

data class GetLandmark(
    val succeeded: Boolean,
    val message: String?,
    val errors: Any?,
    val data: Landmark
)

data class GetHotel(
    val succeeded: Boolean,
    val message: String?,
    val errors: Any?,
    val data: Hotel
)

data class GetHospital(
    val succeeded: Boolean,
    val message: String?,
    val errors: Any?,
    val data: Hospital
)
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String)

data class SignUpRequest(
    val name: String,
    val mail: String,
    val password: String,
    val confirmPassword: String
)

data class LocationResponse(
    val succeeded: Boolean,
    val message: String?,
    val errors: Any?,
    val data: Location
)

data class HospitalSearchResponse(
    val succeeded: Boolean,
    val message: String?,
    val errors: Any?,
    val data: List<Hospital>
)

data class HotelSearchResponse(
    val succeeded: Boolean,
    val message: String?,
    val errors: Any?,
    val data: List<Hotel>
)
data class SignUpResponse(
    val succeeded: Boolean,
    val message: String,
    val errors: Any?,
    val data: String
)

data class RouteRequest(
    val mail: String,
    val hotelId: Int,
    val hospitalId: Int,
    val type: String,
    val routeName: String
)

data class RouteResponse(
    val succeeded: Boolean,
    val message: String?,
    val errors: Any?,
    val data: Int
)

data class RouteListResponse(
    val succeeded: Boolean,
    val message: String?,
    val errors: Any?,
    val data : List<Route>
)


