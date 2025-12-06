package com.picman.picman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.util.Collections;

@SpringBootApplication()
public class PicMan {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PicMan.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8088"));
        app.run(args);
    }
}
