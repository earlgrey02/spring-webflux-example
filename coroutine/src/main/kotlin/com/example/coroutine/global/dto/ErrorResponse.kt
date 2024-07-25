package com.example.coroutine.global.dto

import com.example.coroutine.global.exception.ServerException

data class ErrorResponse(
    val code: Int,
    val message: String
) {
    companion object {
        operator fun invoke(exception: Throwable): ErrorResponse =
            when (exception) {
                is ServerException -> ErrorResponse(code = exception.code, message = exception.message)
                else -> ErrorResponse(code = 500, message = "Internal Server Error")
            }
    }
}
