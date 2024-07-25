package com.example.reactor.kotlinreactor.router

import com.example.reactor.kotlinreactor.handler.UserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class UserRouter {
    @Bean
    fun userRoutes(handler: UserHandler): RouterFunction<ServerResponse> =
        router {
            "/user".nest {
                GET("/{id}", handler::getUserById)
            }
        }
}
