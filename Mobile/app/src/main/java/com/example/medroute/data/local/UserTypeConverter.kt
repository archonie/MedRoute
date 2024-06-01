package com.example.medroute.data.local
//
//import androidx.room.ProvidedTypeConverter
//import androidx.room.TypeConverter
//import com.example.medroute.domain.model.Hospital
//import com.example.medroute.domain.model.Hotel
//import com.example.medroute.domain.model.Landmark
//import com.example.medroute.domain.model.Location
//import com.example.medroute.domain.model.Route
//import com.example.medroute.domain.model.Url
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//
//@ProvidedTypeConverter
//class RouteTypeConverter {
//    private val gson = Gson()
//
//
//    @TypeConverter
//    fun routeToString(route: Route): String {
//        return "${route.id},${route.mail},${route.hotelId},${route.hospitalId},${route.type},${route.routeName}"
//    }
//
//    @TypeConverter
//    fun stringToRoute(route: String): Route {
//        return route.split(",").let {
//            Route(
//                id = it[0].toInt(),
//                mail = it[1],
//                hotelId = it[2].toInt(),
//                hospitalId= it[3].toInt(),
//                type = it[4],
//                routeName = it[5]
//            )
//        }
//    }
//
//    @TypeConverter
//    fun hotelToString(hotel: Hotel): String {
//
//            return "${hotel.id},${hotel.hotelName},${hotel.starRating},${hotel.locationId}"
//
//    }
//
//    @TypeConverter
//    fun stringToHotel(hotel: String): Hotel {
//        return hotel.split(",").let {
//            Hotel(
//                id = it[0].toInt(),
//                hotelName = it[1],
//                starRating = it[2].toDouble(),
//                locationId = it[3].toInt()
//            )
//        }
//    }
//
//
//    @TypeConverter
//    fun fromString(value: String): List<Landmark> {
//        val listType = object : TypeToken<List<Landmark>>() {}.type
//        return gson.fromJson(value, listType)
//    }
//
//    @TypeConverter
//    fun toString(list: List<Landmark?>): String {
//        return gson.toJson(list)
//    }
//
//    @TypeConverter
//    fun hospitalToString(hospital: Hospital): String {
//        return "${hospital.id},${hospital.hospitalName},${hospital.hospitalType},${hospital.locationId}"
//
//
//    }
//
//    @TypeConverter
//    fun stringToHospital(string: String): Hospital {
//        return string.split(",").let {
//            Hospital(
//                id = it[0].toInt(),
//                hospitalName = it[1],
//                hospitalType = it[2],
//                locationId = it[3].toInt()
//            )
//        }
//    }
//
//    @TypeConverter
//    fun landmarkToString(landmark: Landmark): String {
//        return "${landmark.id},${landmark.landmarkName},${landmark.locationId},${landmark.landmarkInfo}"
//    }
//
//    @TypeConverter
//    fun stringToLandmark(landmark: String): Landmark {
//        return landmark.split(",").let {
//            Landmark(
//                id = it[0].toInt(),
//                landmarkName = it[1],
//                locationId = it[2].toInt(),
//                landmarkInfo = it[3]
//            )
//        }
//    }
//
//    @TypeConverter
//    fun locationToString(location: Location): String {
//        return "${location.locationId}.${location.name}.${location.latitude}.${location.longitude}.${location.url}"
//
//    }
//
//    @TypeConverter
//    fun locationFromString(location: String): Location {
//        return location.split(".").let {
//            Location(
//                locationId = it[0].toInt(),
//                name = it[1],
//                latitude = it[2].toDouble(),
//                longitude = it[3].toDouble(),
//                url = it[4]
//            )
//        }
//    }
//
//    @TypeConverter
//    fun urlToString(url: Url): String {
//        return "${url.urlId},${url.url}"
//    }
//
//    @TypeConverter
//    fun urlFromString(url: String): Url {
//        return url.split(",").let {
//            Url(
//                urlId = it[0].toInt(),
//                url = it[1]
//            )
//        }
//    }
//}