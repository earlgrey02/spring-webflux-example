package com.example.reactor.javareactor.global.exception;

import com.example.reactor.javareactor.global.dto.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable exception) {
        log.error(String.format("%s(\"%s\") at %s", exception.getClass().getSimpleName(), exception.getMessage(), exception.getStackTrace()[0]));

        ErrorResponse errorResponse = ErrorResponse.from(exception);
        ServerHttpResponse response = exchange.getResponse();

        response.getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(HttpStatusCode.valueOf(errorResponse.code()));

        try {
            return response.writeWith(
                Mono.just(
                    response.bufferFactory()
                            .wrap(objectMapper.writeValueAsBytes(errorResponse))
                )
            );
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
