package com.example.reactor.kotlinreactor.global.exception

import com.example.reactor.kotlinreactor.global.dto.ErrorResponse
import com.example.reactor.kotlinreactor.global.util.getLogger
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class GlobalExceptionHandler(
    private val objectMapper: ObjectMapper
) : ErrorWebExceptionHandler {
    private val logger = getLogger()

    override fun handle(exchange: ServerWebExchange, exception: Throwable): Mono<Void> =
        Mono.just(exception)
            .doOnNext { logger.error { "${it::class.simpleName}(\"${it.message}\") at ${it.stackTrace[0]}" } }
            .map { ErrorResponse(it) }
            .flatMap {
                exchange.response
                    .apply {
                        headers.contentType = MediaType.APPLICATION_JSON
                        statusCode = HttpStatusCode.valueOf(it.code)
                    }
                    .run {
                        writeWith(
                            Mono.just(
                                bufferFactory()
                                    .wrap(objectMapper.writeValueAsBytes(it))
                            )
                        )
                    }
            }
}
