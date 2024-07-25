package com.example.reactor.javareactor.exception;

import com.example.reactor.javareactor.global.exception.ServerException;

public class UserNotFoundException extends ServerException {
    public UserNotFoundException(String message) {
        super(404, message);
    }

    public UserNotFoundException() {
        super(404, "존재하지 않는 사용자입니다.");
    }
}
