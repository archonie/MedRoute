package com.example.medroute.presentation.login_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medroute.domain.model.UserEntity
import com.example.medroute.domain.usecases.app_entry.LoginUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCases: LoginUseCases
): ViewModel(){

    private val _loginResult = MutableStateFlow<Result<String>?>(null)
    val loginResult: StateFlow<Result<String>?> = _loginResult

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user

    init{
        viewModelScope.launch {
            _user.value = loginUseCases.getUserUseCase()
        }
    }

    fun login(email: String, password: String){
        viewModelScope.launch {
            val result = loginUseCases.loginUseCase(email, password)
            _loginResult.value = result
            Log.d("LoginSuccess"," Login Successful")
        }
    }

    fun clearLoginResult() {
        _loginResult.value = null
    }

}

