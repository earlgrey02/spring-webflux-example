package com.example.reactor.javareactor.repository;

import com.example.reactor.javareactor.entity.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {}
