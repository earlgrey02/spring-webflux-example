package com.example.reactor.javareactor.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@EnableWebFluxSecurity
@Configuration
class SecurityConfiguration {
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http.csrf(CsrfSpec::disable)
                   .formLogin(FormLoginSpec::disable)
                   .httpBasic(HttpBasicSpec::disable)
                   .logout(LogoutSpec::disable)
                   .requestCache(RequestCacheSpec::disable)
                   .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                   .exceptionHandling(spec -> spec.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
                   .authorizeExchange(spec ->
                                          spec.pathMatchers(HttpMethod.POST, "/user")
                                              .permitAll()
                                              .anyExchange()
                                              .authenticated()
                   )
                   .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
