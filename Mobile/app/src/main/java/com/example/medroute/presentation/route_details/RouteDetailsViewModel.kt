package com.example.medroute.presentation.route_details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medroute.domain.model.Hospital
import com.example.medroute.domain.model.Hotel
import com.example.medroute.domain.model.Landmark
import com.example.medroute.domain.model.Location
import com.example.medroute.domain.model.Route
import com.example.medroute.domain.model.UserEntity
import com.example.medroute.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteDetailsViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel(){

    private val _route = MutableStateFlow<Route?>(null)
    val route: StateFlow<Route?> = _route

    private val _hospitalLocation = MutableLiveData<Result<Location>>()
    val hospitalLocation: LiveData<Result<Location>> get() = _hospitalLocation

    private val _hotelLocation = MutableLiveData<Result<Location>>()
    val hotelLocation: LiveData<Result<Location>> get() = _hotelLocation

    private val _landmarkLocations = MutableLiveData<Map<Int, Result<Location>>>()
    val landmarkLocations: LiveData<Map<Int, Result<Location>>> = _landmarkLocations

    private val _landmarkLocation = MutableLiveData<Result<Location>>()
    val landmarkLocation: LiveData<Result<Location>?> get() = _landmarkLocation

    private val _landmarkSuggestions = MutableStateFlow<Result<List<Landmark>>?>(null)
    val landmarkSuggestions: StateFlow<Result<List<Landmark>>?> = _landmarkSuggestions

    private val _hospital = MutableStateFlow<Result<Hospital>?>(null)
    val hospital: StateFlow<Result<Hospital>?> = _hospital

    private val _hotel = MutableStateFlow<Result<Hotel>?>(null)
    val hotel: StateFlow<Result<Hotel>?> = _hotel


    fun getHotel(id: Int){
        viewModelScope.launch {
            _hotel.value = repository.getHotel(id)
        }
    }

    fun getHospital(id:Int){
        viewModelScope.launch {
            _hospital.value = repository.getHospital(id)
        }
    }
    fun getRoute(routeId: Int){
        viewModelScope.launch {
            _route.value = repository.getRoute(routeId)
        }
    }

    fun getHospitalLocation(locationId: Int) {
        viewModelScope.launch {
            val result = repository.getLocation(locationId)
            _hospitalLocation.postValue(result)
        }
    }


    fun getLandmarkLocation(locationId: Int) {
        viewModelScope.launch {
            val result = repository.getLocation(locationId)
            val updatedMap = _landmarkLocations.value.orEmpty() + (locationId to result)
            _landmarkLocations.postValue(updatedMap)
        }
    }
    fun getHotelLocation(locationId: Int) {
        viewModelScope.launch {
            val result = repository.getLocation(locationId)
            _hotelLocation.postValue(result)
        }
    }

    fun getLandmarkSuggestions(latitude: Double, longitude: Double, radius: Double, requestedNum: Int = 10){
        viewModelScope.launch{
            val suggestions = repository.searchLandmarks(latitude, longitude, radius, requestedNum)
            suggestions?.getOrNull()?.toString()?.let { Log.d("landmarks", it) }
            _landmarkSuggestions.value = suggestions
        }
    }

}