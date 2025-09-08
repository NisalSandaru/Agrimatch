package com.Nisal.Agrimatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // disable CSRF for testing API
                .authorizeHttpRequests()
                .requestMatchers("/api/users/register","/api/users/login").permitAll() // allow register without login
                .anyRequest().authenticated() // all other requests need authentication
                .and()
                .httpBasic(); // optional for testing
        return http.build();
    }
}
