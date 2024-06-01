package com.example.medroute.domain.usecases.app_entry

import com.example.medroute.domain.model.UserEntity
import com.example.medroute.domain.repository.UserRepository

class GetUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): UserEntity?{
        return userRepository.getUser()
    }
}
