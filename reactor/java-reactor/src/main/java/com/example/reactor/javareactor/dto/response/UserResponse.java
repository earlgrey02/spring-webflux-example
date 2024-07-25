package com.example.reactor.javareactor.dto.response;

import com.example.reactor.javareactor.entity.User;
import lombok.Builder;

@Builder
public record UserResponse(
    String id,
    String name
) {
    public static UserResponse from(User user) {
        return UserResponse.builder()
                           .id(user.getId())
                           .name(user.getName())
                           .build();
    }
}
