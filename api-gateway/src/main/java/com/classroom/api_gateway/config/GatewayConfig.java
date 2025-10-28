package com.classroom.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder){

        return builder.routes()
                .route("user-service", r -> r.path("/api/users/**")
                        .uri("lb://USER-SERVICE"))
                .route("classroom-service", r -> r.path("/api/classroom/**")
                        .uri("lb://CLASSROOM-SERVICE"))
                .route("stream-service", r -> r.path("/api/stream/**")
                        .uri("lb://STREAM-SERVICE"))
                .build();
    }
}
