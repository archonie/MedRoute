package com.example.medroute.presentation.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medroute.domain.model.Hospital
import com.example.medroute.domain.model.Hotel
import com.example.medroute.domain.model.Route
import com.example.medroute.domain.model.UserEntity
import com.example.medroute.domain.repository.UserRepository
import com.example.medroute.domain.usecases.app_entry.LoginUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val loginUseCases: LoginUseCases,
    private val repository: UserRepository
) :ViewModel(){

    private val _routeList = MutableStateFlow<List<Route>>(emptyList())
    val routeList: StateFlow<List<Route>> = _routeList
    private val _routes = MutableStateFlow<Result<List<Route>>?>(null)
    val routes: StateFlow<Result<List<Route>>?> = _routes

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user

    private val _hospital = MutableStateFlow<Result<Hospital>?>(null)
    val hospital: StateFlow<Result<Hospital>?> = _hospital

    private val _hotel = MutableStateFlow<Result<Hotel>?>(null)
    val hotel: StateFlow<Result<Hotel>?> = _hotel


    init {
        viewModelScope.launch {
            getUser()
        }

    }

    fun getHospital(id: Int){
        viewModelScope.launch {
            _hospital.value = repository.getHospital(id)
        }
    }
    fun getHotel(id:Int){
        viewModelScope.launch {
            _hotel.value = repository.getHotel(id)
        }
    }

    suspend fun logOut(user: UserEntity){
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }


    fun getRoutes(mail: String){
        viewModelScope.launch {
            _routes.value = repository.getAllRoutes(mail)
        }
    }



    fun getUser(){
        viewModelScope.launch {
            _user.value = loginUseCases.getUserUseCase()
        }
    }
    fun deleteRoute(id: Int, mail: String){
        viewModelScope.launch {
            repository.deleteRoute(id)
            getRoutes(mail)
        }
    }

    fun getRoutesFromDatabase(){
        viewModelScope.launch {
            _routeList.value = repository.getRoutes()
        }
    }
}