package com.example.reactor.kotlinreactor.dto.request

data class CreateUserRequest(
    val email: String,
    val password: String,
    val name: String
)
