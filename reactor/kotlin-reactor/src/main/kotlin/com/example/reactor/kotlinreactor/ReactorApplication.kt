package com.example.reactor.kotlinreactor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReactorApplication

fun main(args: Array<String>) {
    runApplication<ReactorApplication>(*args)
}
