package com.mealdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * TheMealDB Explorer – Spring Boot Application Entry Point
 *
 * <p>Starts an embedded Tomcat server on port 8080 exposing REST endpoints
 * that proxy and cache responses from the public TheMealDB API.
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class MealDbExplorerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MealDbExplorerApplication.class, args);
    }
}
