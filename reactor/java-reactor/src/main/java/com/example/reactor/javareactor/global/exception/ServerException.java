package com.example.reactor.javareactor.global.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {
    private final int code;
    private final String message;

    @Builder
    public ServerException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
