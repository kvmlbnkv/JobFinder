package com.it.jobfinder.security;

import com.it.jobfinder.entities.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity //To dla @PreAuthorize
public class WebSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTGenerator jwtGenerator;
    private final JWTVerifier jwtVerifier;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(new CustomAuthenticationFilter(this.authenticationManager(authenticationConfiguration), jwtGenerator))
                .addFilterAfter(new JWTVerificationFilter(jwtVerifier), CustomAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/*", "/skill/add", "/skill/delete", "/user/*")
                            .hasAuthority(UserRole.ADMIN.name())
                        .requestMatchers("/application/make", "/application/user",
                                "/employee/delete", "/employee/addSkill", "/employee/removeSkill", "/employee/update")
                            .hasAnyAuthority(UserRole.EMPLOYEE.name(), UserRole.ADMIN.name())
                        .requestMatchers("/job/add", "/job/update", "/job/close",
                                "/application/job",
                                "/employer/delete", "/employer/update")
                            .hasAnyAuthority(UserRole.EMPLOYER.name(), UserRole.ADMIN.name())
                        .requestMatchers("/employee/get*", "/employer/get*", "/job/get*", "/skill/get*")
                            .authenticated()
                        .requestMatchers( "*/register", "/login")
                            .permitAll()
                        .anyRequest().authenticated()
                        )
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
