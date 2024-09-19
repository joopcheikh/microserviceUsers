package com.getusers.getusers;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@EnableDiscoveryClient
public class GetUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(GetUserApplication.class, args);
    }

}
