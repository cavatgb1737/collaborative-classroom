package com.classroom.classroom_service.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class JwtUtil {

    private final static String SECRET = "abcdefaerhtawygityiawrtiuawthawutrhawuotfgahoiawrtarwtawtwat";
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());


    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    public static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public static String getClaim(String token, String claimKey) {
        Claims claims = getClaims(token);
        Object val = claims.get(claimKey);
        return val != null ? val.toString() : null;
    }


    public static String getName(String token) {
        return getClaim(token, "name");
    }

    public static String getEmail(String token) {
        return getClaim(token, "email");
    }

    public static String getSubject(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public static String getImageUrl(String token) {
        return getClaim(token, "imageUrl");
    }
}
