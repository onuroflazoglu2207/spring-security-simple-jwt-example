package com.example.demo.util;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.example.demo.config.JwtConfig;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

    private String signKey() {
        return "LoginKey";
    }

    public String getSubject(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getIssuedAt(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpiration(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims == null ? null : claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            if (token == null) return null;
            return Jwts.parser().setSigningKey(signKey().getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpiration(token);
        if (expiration == null) return Boolean.TRUE;
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        if (userDetails == null) return null;
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        claims.put("password", userDetails.getPassword());
        claims.put("authorities", userDetails.getAuthorities());
        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JwtConfig.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, signKey().getBytes(StandardCharsets.UTF_8)).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        if (token == null || userDetails == null) return Boolean.FALSE;
        Claims claims = getAllClaimsFromToken(token);
        boolean u = claims.get("username").equals(userDetails.getUsername());
        boolean p = claims.get("password").equals(userDetails.getPassword());
        boolean e = !isTokenExpired(token);
        return u && p && e;
    }
}
