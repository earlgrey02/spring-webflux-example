package com.example.reactor.javareactor.service;

import com.example.reactor.javareactor.dto.response.UserResponse;
import com.example.reactor.javareactor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Mono<UserResponse> getUserById(final String id) {
        return userRepository.findById(id)
                             .map(UserResponse::from);
    }
}
