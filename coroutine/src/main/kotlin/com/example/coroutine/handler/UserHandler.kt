package com.example.coroutine.handler

import com.example.coroutine.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class UserHandler(
    private val userService: UserService
) {
    suspend fun getUserById(request: ServerRequest): ServerResponse =
        ServerResponse.ok()
            .bodyValueAndAwait(userService.getUserById(request.pathVariable("id")))
}
