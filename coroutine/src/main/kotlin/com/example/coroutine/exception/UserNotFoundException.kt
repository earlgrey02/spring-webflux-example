package com.example.coroutine.exception

import com.example.coroutine.global.exception.ServerException

data class UserNotFoundException(
    override val message: String = "존재하지 않는 사용자입니다."
) : ServerException(code = 404, message)
