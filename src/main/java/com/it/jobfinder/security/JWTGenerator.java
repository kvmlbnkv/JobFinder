package com.it.jobfinder.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTGenerator {
    private final String key;
    private final String tokenLength;

    public JWTGenerator(@Value("${jwt.token.key}")String key, @Value("${jwt.token.tokenLength}")String tokenLength) {
        this.key = key;
        this.tokenLength = tokenLength;
    }

    public String generate(Authentication authentication){
        return Jwts.builder()
                .subject(authentication.getName())
                .claim("authorities", authentication.getAuthorities())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + Integer.parseInt(this.tokenLength)))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.key)))
                .compact();
    }
}
