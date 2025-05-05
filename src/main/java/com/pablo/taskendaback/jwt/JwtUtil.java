package com.pablo.taskendaback.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(Authentication authentication){
        UserDetails mainUser = (UserDetails) authentication.getPrincipal();
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder().setSubject(mainUser.getUsername()).setIssuedAt(new Date()).setExpiration(new Date(new Date().getTime() + expiration * 1000L)).signWith(key,SignatureAlgorithm.HS256).compact();
    }

    public Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token){
        return extractAllClaims(token).getExpiration();
    }

    public Claims extractAllClaims(String token){
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token){
        return extractAllClaims(token).getSubject();
    }
}
