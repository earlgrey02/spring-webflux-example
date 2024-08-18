package com.example.reactor.kotlinreactor.dto.response

import com.example.reactor.kotlinreactor.entity.User

data class UserResponse(
    val id: String,
    val email: String,
    val name: String
) {
    companion object {
        operator fun invoke(user: User): UserResponse =
            with(user) {
                UserResponse(
                    id = id!!,
                    email = email,
                    name = name
                )
            }
    }
}
