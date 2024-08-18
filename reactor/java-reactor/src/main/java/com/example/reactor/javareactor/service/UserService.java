package com.example.reactor.javareactor.service;

import com.example.reactor.javareactor.dto.request.CreateUserRequest;
import com.example.reactor.javareactor.dto.response.UserResponse;
import com.example.reactor.javareactor.entity.User;
import com.example.reactor.javareactor.exception.UserAlreadyExistException;
import com.example.reactor.javareactor.exception.UserNotFoundException;
import com.example.reactor.javareactor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<UserResponse> getUserById(final String id) {
        return userRepository.findById(id)
                             .switchIfEmpty(Mono.error(new UserNotFoundException()))
                             .map(UserResponse::from);
    }

    public Mono<UserResponse> createUser(final CreateUserRequest request) {
        return userRepository.findByEmail(request.email())
                             .flatMap(user -> Mono.<User>error(new UserAlreadyExistException()))
                             .switchIfEmpty(
                                 Mono.defer(
                                     () -> userRepository.save(
                                         User.builder()
                                             .email(request.email())
                                             .password(passwordEncoder.encode(request.password()))
                                             .name(request.name())
                                             .build()
                                     )
                                 )
                             )
                             .map(UserResponse::from);
    }
}
