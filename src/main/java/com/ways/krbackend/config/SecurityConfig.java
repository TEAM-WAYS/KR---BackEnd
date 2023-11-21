package com.ways.krbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests((requests) -> {
                    requests.requestMatchers("/user").authenticated()
                    //requests.requestMatchers("/myAccount").hasRole("ADMIN")
                            //.requestMatchers("/myBalance").hasRole("ADMIN,SALES")
                            .requestMatchers("/application").permitAll();
                })

                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());
        //return (SecurityFilterChain)http.build();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder();
    }



}
