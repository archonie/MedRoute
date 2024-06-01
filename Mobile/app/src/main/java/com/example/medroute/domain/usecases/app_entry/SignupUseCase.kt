package com.example.medroute.domain.usecases.app_entry

import com.example.medroute.domain.repository.UserRepository

class SignupUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String, confirmPassword:String, name: String): Result<Boolean>{
        return userRepository.signUp(
            mail = email,
            password = password,
            confirmPassword = confirmPassword,
            name = name
        )
    }
}
