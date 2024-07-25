package com.example.reactor.kotlinreactor.service

import com.example.reactor.kotlinreactor.dto.response.UserResponse
import com.example.reactor.kotlinreactor.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun getUserById(id: String): Mono<UserResponse> =
        userRepository.findById(id)
            .map { UserResponse(it) }
}
