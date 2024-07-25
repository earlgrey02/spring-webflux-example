package com.example.reactor.kotlinreactor.repository

import com.example.reactor.kotlinreactor.entity.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : ReactiveMongoRepository<User, String>
