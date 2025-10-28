package com.classroom.api_gateway;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
public class GoogleOAuth2Login {

    @GetMapping("/login/google")
    public Mono<Void> loginWithGoogle(ServerWebExchange exchange){
        new WebSessionServerRequestCache().removeMatchingRequest(exchange);

        exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
        exchange.getResponse().getHeaders().setLocation(URI.create("/oauth2/authorization/google"));
        return exchange.getResponse().setComplete();
    }

}
