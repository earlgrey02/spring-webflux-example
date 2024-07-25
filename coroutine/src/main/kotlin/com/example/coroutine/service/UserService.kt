package com.example.coroutine.service

import com.example.coroutine.dto.response.UserResponse
import com.example.coroutine.exception.UserNotFoundException
import com.example.coroutine.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    suspend fun getUserById(id: String): UserResponse =
        userRepository.findById(id)
            ?.let { UserResponse(it) }
            ?: throw UserNotFoundException()
}
