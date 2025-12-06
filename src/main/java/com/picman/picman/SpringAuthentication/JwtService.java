package com.picman.picman.SpringAuthentication;
import java.security.Key;
import java.util.Collection;
import java.util.Date;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtService {
    private     final   String      secret;
    @Getter
    private     final   long        jwtExpiration;
    private     final   Logger      logger;

    public JwtService() {
        secret = "9D0EB6B1C2E1FAD0F53A248F6C3B5E4E2F6D8G3H1I0J7K4L1M9N2O3P5Q0R7S9T1U4V2W6X0Y3Z";
        jwtExpiration = 1000 * 60 * 60;
        logger = LoggerFactory.getLogger(this.getClass());
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email, Collection<? extends GrantedAuthority> authorities) {
        return Jwts.builder()
                .setSubject(email)
                .claim("roles", authorities.stream().map(GrantedAuthority::getAuthority).toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUserMail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build().
                parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String email, String token) {
        return email.equals(extractUserMail(token)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
