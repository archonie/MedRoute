package com.example.medroute.domain.usecases.app_entry

data class LoginUseCases(
    val loginUseCase: LoginUseCase,
    val getUserUseCase: GetUserUseCase,
    val signupUseCase: SignupUseCase
)
