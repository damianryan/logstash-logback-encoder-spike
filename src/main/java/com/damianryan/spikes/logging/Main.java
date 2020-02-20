package com.damianryan.spikes.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Main implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("no errors yet");
        try {
            throw new RuntimeException("Boom!");
        } catch (Exception x) {
            log.error("something really bad happened", x);
        }
        log.info("you should have seen an error logged");
    }
}
