package com.example.reactor.javareactor.global.dto;


import com.example.reactor.javareactor.global.exception.ServerException;
import lombok.Builder;

@Builder
public record ErrorResponse(
    int code,
    String message
) {
    public static ErrorResponse from(Throwable exception) {
        boolean isServerException = exception instanceof ServerException;

        return ErrorResponse.builder()
                            .code(isServerException ? ((ServerException) exception).getCode() : 500)
                            .message(isServerException ? ((ServerException) exception).getMessage() : "Internal Server Error")
                            .build();
    }
}
