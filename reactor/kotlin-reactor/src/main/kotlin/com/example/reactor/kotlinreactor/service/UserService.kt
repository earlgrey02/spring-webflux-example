package com.example.reactor.kotlinreactor.service

import com.example.reactor.kotlinreactor.dto.request.CreateUserRequest
import com.example.reactor.kotlinreactor.dto.response.UserResponse
import com.example.reactor.kotlinreactor.entity.User
import com.example.reactor.kotlinreactor.exception.UserAlreadyExistException
import com.example.reactor.kotlinreactor.exception.UserNotFoundException
import com.example.reactor.kotlinreactor.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun getUserById(id: String): Mono<UserResponse> =
        userRepository.findById(id)
            .switchIfEmpty(Mono.error(UserNotFoundException()))
            .map { UserResponse(it) }

    fun createUser(request: CreateUserRequest): Mono<UserResponse> =
        with(request) {
            userRepository.findByEmail(email)
                .flatMap { Mono.error<User>(UserAlreadyExistException()) }
                .switchIfEmpty(
                    Mono.defer {
                        userRepository.save(
                            User(
                                email = email,
                                password = passwordEncoder.encode(password),
                                name = name
                            )
                        )
                    }
                )
                .map { UserResponse(it) }
        }
}
