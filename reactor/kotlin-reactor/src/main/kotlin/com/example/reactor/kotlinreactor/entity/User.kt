package com.example.reactor.kotlinreactor.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(
    @Id
    val id: String? = null,
    val email: String,
    val password: String,
    val name: String
)
