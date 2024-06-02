package com.it.jobfinder.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTVerifier {

    private final String key;

    public JWTVerifier(@Value("${jwt.token.key}")String key){
        this.key = key;
    }

    public Claims validate(String token){
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.key)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
