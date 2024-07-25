package com.example.reactor.kotlinreactor.exception

import com.example.reactor.kotlinreactor.global.exception.ServerException

data class UserNotFoundException(
    override val message: String = "존재하지 않는 사용자입니다."
) : ServerException(code = 404, message)
