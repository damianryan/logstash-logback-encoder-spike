package com.damianryan.spikes.logging

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LoggingApp

fun main(args: Array<String>) {
    runApplication<LoggingApp>(*args)
}