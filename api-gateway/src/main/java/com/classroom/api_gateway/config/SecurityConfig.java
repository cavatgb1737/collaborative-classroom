package com.classroom.api_gateway.config;

import com.classroom.api_gateway.UserEvent;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.savedrequest.ServerRequestCache;
import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.time.Duration;
import java.util.Arrays;

@Slf4j
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ServerRequestCache requestCache = new WebSessionServerRequestCache();

    private final String jwtSecret = "abcdefaerhtawygityiawrtiuawthawutrhawuotfgahoiawrtarwtawtwat";
    private final long jwtExpirationMs = 3600000 * 2;

    public SecurityConfig(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173"
        ));
        corsConfig.setAllowedMethods(Arrays.asList(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name()
        ));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(Duration.ofHours(1));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    @Bean
    public SecurityWebFilterChain swfc(ServerHttpSecurity http, ReactiveClientRegistrationRepository rep){
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/", "/login/**", "/oauth2/**", "/api/**", "/ws/**").permitAll()
                        .anyExchange().authenticated())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutHandler((exchange, auth) -> {
                            exchange.getExchange().getResponse().addCookie(ResponseCookie.from("JWT", "")
                                    .path("/")
                                    .maxAge(Duration.ZERO)
                                    .httpOnly(true)
                                    .build());
                            return Mono.empty();
                        })
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationRequestResolver(authorizationRequestResolver(rep))
                        .authenticationSuccessHandler(successHandler()));
        return http.build();
    }

    private ServerAuthenticationSuccessHandler successHandler() {
        return (exchange, authentication) -> {

            OAuth2User principal = (OAuth2User) authentication.getPrincipal();

            UserEvent event = new UserEvent();
            event.setName(principal.getAttribute("name"));
            event.setEmail(principal.getAttribute("email"));
            event.setImageUrl(principal.getAttribute("picture"));

            Mono.fromRunnable(() -> {
                kafkaTemplate.send("user-topic", event);
                log.info("Event sent to Kafka: {}", event);
            }).subscribe();

            Map<String, Object> claims = Map.of(
                    "name", principal.getAttribute("name"),
                    "email", principal.getAttribute("email"),
                    "imageUrl", principal.getAttribute("picture")
            );

            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(principal.getAttribute("email"))
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            ResponseCookie cookie = ResponseCookie.from("JWT", token)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofHours(2))
                    .sameSite("Lax")
                    .secure(false)
                    .build();

            exchange.getExchange().getResponse().addCookie(cookie);

            return requestCache.getRedirectUri(exchange.getExchange())
                    .defaultIfEmpty(URI.create("http://localhost:5173"))
                    .flatMap(originalUri -> {
                        String originalUrl = originalUri.toString();
                        RedirectServerAuthenticationSuccessHandler redirectHandler =
                                new RedirectServerAuthenticationSuccessHandler(originalUrl);
                        return redirectHandler.onAuthenticationSuccess(exchange, authentication);
                    });
        };
    }

    @Bean
    public ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ReactiveClientRegistrationRepository repo) {

        DefaultServerOAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultServerOAuth2AuthorizationRequestResolver(repo);

        return new ServerOAuth2AuthorizationRequestResolver() {
            @Override
            public Mono<OAuth2AuthorizationRequest> resolve(ServerWebExchange exchange) {
                return defaultResolver.resolve(exchange)
                        .map(req -> OAuth2AuthorizationRequest.from(req)
                                .additionalParameters(Map.of("prompt", "select_account"))
                                .build());
            }

            @Override
            public Mono<OAuth2AuthorizationRequest> resolve(ServerWebExchange exchange, String clientRegistrationId) {
                return defaultResolver.resolve(exchange, clientRegistrationId)
                        .map(req -> OAuth2AuthorizationRequest.from(req)
                                .additionalParameters(Map.of("prompt", "select_account"))
                                .build());
            }
        };
    }



}
