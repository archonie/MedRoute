package com.example.medroute.domain.usecases.app_entry

import com.example.medroute.domain.repository.UserRepository

class LoginUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String) :Result<String>{
        return userRepository.login(email, password)
    }
}