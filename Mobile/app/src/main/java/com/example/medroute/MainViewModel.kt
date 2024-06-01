package com.example.medroute

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medroute.domain.model.UserEntity
import com.example.medroute.domain.usecases.app_entry.LoginUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginUseCases: LoginUseCases
) : ViewModel() {

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user


    init {
        viewModelScope.launch {
            getUser()
        }

    }

    fun getUser(){
        viewModelScope.launch {
            _user.value = loginUseCases.getUserUseCase()
        }
    }
}