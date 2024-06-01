package com.example.medroute.presentation.landmark_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medroute.domain.model.Landmark
import com.example.medroute.domain.model.Location
import com.example.medroute.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandmarkViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _landmark = MutableLiveData<Result<Landmark?>>()
    val landmark: LiveData<Result<Landmark?>> = _landmark

    private val _landmarkLocation = MutableLiveData<Result<Location?>>()
    val landmarkLocation: LiveData<Result<Location?>> = _landmarkLocation

    fun getLandmark(id: Int) {
        viewModelScope.launch {
            val result = repository.getLandmark(id)
            _landmark.postValue(result)
        }
    }

    fun getLocation(landmarkId: Int) {
        viewModelScope.launch {
            val result = repository.getLocation(landmarkId)
            _landmarkLocation.postValue(result)
        }
    }
}