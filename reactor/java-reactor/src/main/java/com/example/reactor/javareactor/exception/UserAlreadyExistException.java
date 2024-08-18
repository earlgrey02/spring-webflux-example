package com.example.reactor.javareactor.exception;

import com.example.reactor.javareactor.global.exception.ServerException;

public class UserAlreadyExistException extends ServerException {
    public UserAlreadyExistException(String message) {
        super(404, message);
    }

    public UserAlreadyExistException() {
        super(409, "이미 존재하는 계정입니다.");
    }
}
