package com.example.reactor.javareactor.handler;

import com.example.reactor.javareactor.dto.request.CreateUserRequest;
import com.example.reactor.javareactor.dto.response.UserResponse;
import com.example.reactor.javareactor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class UserHandler {
    private final UserService userService;

    public Mono<ServerResponse> getUserById(ServerRequest request) {
        return ServerResponse.ok()
                             .body(userService.getUserById(request.pathVariable("id")), UserResponse.class);
    }

    public Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(CreateUserRequest.class)
                      .flatMap(createUserRequest ->
                                   ServerResponse.ok()
                                                 .body(userService.createUser(createUserRequest), UserResponse.class)
                      );
    }
}
