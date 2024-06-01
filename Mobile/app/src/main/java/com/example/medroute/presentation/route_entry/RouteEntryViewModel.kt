package com.example.medroute.presentation.route_entry

import android.location.Location
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medroute.domain.model.Hospital
import com.example.medroute.domain.model.Hotel
import com.example.medroute.domain.model.Route
import com.example.medroute.domain.model.UserEntity
import com.example.medroute.domain.repository.UserRepository
import com.example.medroute.domain.usecases.app_entry.LoginUseCases
import com.example.medroute.util.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteEntryViewModel @Inject constructor(
    private val locationUtils: LocationUtils,
    private val repository: UserRepository,
    private val loginUseCases: LoginUseCases

) : ViewModel() {
    private val _currentLocation = mutableStateOf<Location?>(null)
    val currentLocation: State<Location?> = _currentLocation

    private val _hotelSuggestions = MutableStateFlow<Result<List<Hotel>>?>(null)
    val hotelSuggestions: StateFlow<Result<List<Hotel>>?> = _hotelSuggestions
    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user

    private val _hospitalSuggestions = MutableStateFlow<Result<List<Hospital>>?>(null)
    val hospitalSuggestions: StateFlow<Result<List<Hospital>>?> = _hospitalSuggestions


    private val _routePostSuccess = MutableStateFlow(false)
    val routePostSuccess: StateFlow<Boolean> = _routePostSuccess

    init {
        viewModelScope.launch {
            _user.value = loginUseCases.getUserUseCase()
        }
    }

    fun fetchCurrentLocation() {
        locationUtils.getCurrentLocation { location ->
            _currentLocation.value = location
        }
    }


    fun searchHotelSuggestions(query: String) {
        viewModelScope.launch {
            val result = repository.searchHotels(query)
            _hotelSuggestions.value = result
        }
    }

    fun searchHospitalSuggestions(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val suggestions = repository.searchHospitals(query)
            _hospitalSuggestions.value = suggestions

        }
    }

    fun updateLocation(location: Location) {
        _currentLocation.value = location
    }

    fun insertRoute(hotel: Hotel, hospital: Hospital, userMail: String) {
        viewModelScope.launch {
            val response = repository.postRoute(
                mail = userMail,
                hotelId = hotel.id,
                hospitalId = hospital.id,
                type = "Health",
                routeName = "Route"
            )
            if (response.isSuccess) {
                response.getOrNull()?.let { id ->
                    repository.insertRoute(
                        Route(
                            id = id,
                            hotelId = hotel.id,
                            hospitalId = hospital.id,
                            type = "Health",
                            mail = userMail,
                            routeName = "Route"
                        )
                    )
                    _routePostSuccess.value = true
                }
            } else {
                Log.e("InsertRoute", "Failed to insert route: ${response.exceptionOrNull()?.message}")
                _routePostSuccess.value = false
            }
        }
    }


}
