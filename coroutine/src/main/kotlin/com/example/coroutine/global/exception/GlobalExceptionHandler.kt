package com.example.coroutine.global.exception

import com.example.coroutine.global.dto.ErrorResponse
import com.example.coroutine.global.util.getLogger
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class GlobalExceptionHandler(
    private val objectMapper: ObjectMapper
) : ErrorWebExceptionHandler {
    private val logger = getLogger()

    override fun handle(exchange: ServerWebExchange, exception: Throwable): Mono<Void> =
        with(exchange.response) {
            val response = ErrorResponse(exception)

            logger.error { "${exception::class.simpleName}(\"${exception.message}\") at ${exception.stackTrace[0]}" }

            headers.contentType = MediaType.APPLICATION_JSON
            statusCode = HttpStatusCode.valueOf(response.code)

            writeBody(response)
        }

    private fun ServerHttpResponse.writeBody(body: Any): Mono<Void> =
        writeWith(
            Mono.just(
                bufferFactory()
                    .wrap(objectMapper.writeValueAsBytes(body))
            )
        )
}
