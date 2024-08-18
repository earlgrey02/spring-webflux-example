package com.example.coroutine.service

import com.example.coroutine.dto.request.CreateUserRequest
import com.example.coroutine.dto.response.UserResponse
import com.example.coroutine.entity.User
import com.example.coroutine.exception.UserAlreadyExistException
import com.example.coroutine.exception.UserNotFoundException
import com.example.coroutine.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    suspend fun getUserById(id: String): UserResponse =
        userRepository.findById(id)
            ?.let { UserResponse(it) }
            ?: throw UserNotFoundException()

    suspend fun createUser(request: CreateUserRequest): UserResponse =
        with(request) {
            userRepository.findByEmail(email)
                ?.run { throw UserAlreadyExistException() }

            val user = userRepository.save(
                User(
                    email = email,
                    password = passwordEncoder.encode(password),
                    name = name
                )
            )

            return UserResponse(user)
        }
}
