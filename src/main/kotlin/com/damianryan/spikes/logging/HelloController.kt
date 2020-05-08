package com.damianryan.spikes.logging

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class HelloController {

    @GetMapping("/hello/{name}")
    fun hello(@PathVariable name: String?, request: HttpServletRequest): ResponseEntity<String> {
        return try {
            MDC.put("method", request.method)
            MDC.put("protocol", request.protocol)
            MDC.put("request_uri", request.requestURI)
            MDC.put("remote_user", request.remoteUser)
            MDC.put("remote_addr", request.remoteAddr)
            val response = ResponseEntity.ok(String.format("Hello, %s", name))
            MDC.put("status_code", response.statusCode.toString())
            log.info("returned {}", response.body)
            response
        } finally {
            MDC.clear()
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(HelloController::class.java)
    }
}