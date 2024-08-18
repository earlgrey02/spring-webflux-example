package com.example.reactor.javareactor.dto.request;

import lombok.Builder;

@Builder
public record CreateUserRequest(
    String email,
    String password,
    String name
) {}
