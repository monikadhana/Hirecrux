package com.hirecrux_backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;

@Service
public class JwtService {

    @Value("${jwt.secrt}")
    private String secreteKey;

    @Value("${jwt.expiration}")
    private long expiration;
    
}
