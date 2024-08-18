package com.example.reactor.javareactor.dto.response;

import com.example.reactor.javareactor.entity.User;
import lombok.Builder;

@Builder
public record UserResponse(
    String id,
    String email,
    String name
) {
    public static UserResponse from(User user) {
        return UserResponse.builder()
                           .id(user.getId())
                           .email(user.getEmail())
                           .name(user.getName())
                           .build();
    }
}
