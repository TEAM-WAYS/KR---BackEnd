package com.ways.krbackend.service;


import com.ways.krbackend.constant.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class jwtTokenServiceImp implements JwtTokenService {
    public String generateJwtToken(Authentication authentication) {
        SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toSet());

        return Jwts.builder()
                .setIssuer("Kassow robots")
                .setSubject("JWT Token")
                .claim("username", authentication.getName())
                .claim("authorities", String.join(",", authorities))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 30000000))
                .signWith(key)
                .compact();
    }
}

