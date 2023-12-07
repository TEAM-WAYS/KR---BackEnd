package com.ways.krbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity(debug = true)
public class KrBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(KrBackEndApplication.class, args);
    }

}
