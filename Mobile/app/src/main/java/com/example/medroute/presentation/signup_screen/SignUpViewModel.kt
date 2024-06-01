package com.example.medroute.presentation.signup_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medroute.domain.usecases.app_entry.LoginUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val loginUseCases: LoginUseCases
): ViewModel() {

    private val _signupResult = MutableStateFlow<Result<Boolean>?>(null)
    val signupResult: StateFlow<Result<Boolean>?> = _signupResult


    fun signUp(email: String, password: String, confirmPassword:String, name: String){
        viewModelScope.launch {
            val result = loginUseCases.signupUseCase.invoke(
                email = email,password = password, confirmPassword = confirmPassword,name = name
            )
            _signupResult.value = result
            Log.d("SignUp", "$result")
        }
    }


    fun clearResult(){
        _signupResult.value = null
    }



}