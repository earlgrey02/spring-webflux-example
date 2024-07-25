package com.example.reactor.kotlinreactor.global.exception

abstract class ServerException(
    val code: Int,
    override val message: String
) : RuntimeException(message)
