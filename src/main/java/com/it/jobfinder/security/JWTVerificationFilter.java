package com.it.jobfinder.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

@RequiredArgsConstructor
@Component
public class JWTVerificationFilter extends OncePerRequestFilter {

    private final JWTVerifier jwtVerifier;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;

        if (header != null && header.startsWith("Bearer")){
            token = header.substring(7);
        }

        if (token != null){
            try {
                Claims claims = jwtVerifier.validate(token);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, getAuthorities(claims));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            catch (JwtException e){
                response.setStatus(401);
                response.getOutputStream().write("Invalid token".getBytes());
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        return request.getServletPath().equals("/login") || request.getServletPath().equals("/user/register");
    }


    protected Collection<? extends GrantedAuthority> getAuthorities(Claims claims){
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        Collection<?> tokenAuthorities = claims.get("authorities", Collection.class);
        for (Object auth : tokenAuthorities){
            LinkedHashMap<String, String> authMap = (LinkedHashMap<String, String>) auth;
            authorities.add(new SimpleGrantedAuthority(authMap.get("authority")));
        }

        return authorities;
    }

}
