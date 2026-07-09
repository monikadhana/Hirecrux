package com.hirecrux_backend.security;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    //WT is simply a long encoded string.
    public String generateToken(String email){
        //.getBytes(StandardCharsets.UTF_8)--Converts your text string into a raw array of bytes using standard UTF-8 encoding.
        //Keys.hmacShaKeyFor(...): This takes those raw bytes and wraps them into an official SecretKey object using the HMAC-SHA algorithm.
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder().subject(email).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + expiration)).signWith(key).compact();
        //.signWith(key)-- Signature, compact() --Finish building and convert everything into one JWT string.
    }

    //checks the token is our generated token and parseSignedClaims varifiy the sign, header,payload separate and validity
    public String extractEmail(String token){
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public boolean isTokenValid(String token){
        if (isTokenExpired(token)){
            throw new RuntimeException("token is expired");
        }
        return true;
    }

    //the token is expired or not compared to current time > Expiration time
    public boolean isTokenExpired(String token){
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Date expirationDate = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getExpiration();
        return expirationDate.before(new Date());
    }
}
