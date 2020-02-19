package com.damianryan.spikes.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
public class HelloController {

    @GetMapping("/hello/{name}")
    public ResponseEntity<String> hello(@PathVariable final String name, final HttpServletRequest request) {
        MDC.put("method", request.getMethod());
        MDC.put("protocol", request.getProtocol());
        MDC.put("request_url", request.getRequestURL().toString());
        MDC.put("remote_user", request.getRemoteUser());
        MDC.put("remote_addr", request.getRemoteAddr());

        ResponseEntity<String> response = ResponseEntity.ok(String.format("Hello, %s", name));
        MDC.put("status_code", response.getStatusCode().toString());
        log.info("returned {}", response.getBody());
        return response;
    }
}
