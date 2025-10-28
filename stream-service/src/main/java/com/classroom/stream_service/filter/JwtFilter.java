package com.classroom.stream_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        String token = null;
        if(req.getCookies() != null){
            for(Cookie cookie : req.getCookies()){
                if("JWT".equals(cookie.getName())){
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if(token != null && SecurityContextHolder.getContext().getAuthentication() == null){
            if(JwtUtil.validateToken(token)){
                String email = JwtUtil.getSubject(token);
                Authentication auth = new UsernamePasswordAuthenticationToken(email, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("Valid token received: {}", token);
            }
        }

        filterChain.doFilter(req, res);
    }

}
