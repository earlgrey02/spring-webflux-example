package com.example.reactor.kotlinreactor.handler

import com.example.reactor.kotlinreactor.dto.request.CreateUserRequest
import com.example.reactor.kotlinreactor.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

@Component
class UserHandler(
    private val userService: UserService
) {
    fun getUserById(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(userService.getUserById(request.pathVariable("id")))

    fun createUser(request: ServerRequest): Mono<ServerResponse> =
        request.bodyToMono<CreateUserRequest>()
            .flatMap {
                ServerResponse.ok()
                    .body(userService.createUser(it))
            }
}
