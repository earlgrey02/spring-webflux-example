package com.example.reactor.javareactor.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document
public class User {
    @Id
    private String id;
    private String name;

    @Builder
    public User(String name) {
        this.name = name;
    }
}
