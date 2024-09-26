package org.copper.users.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.copper.users.exception.RequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    @Override
    public String getToken(UserDetails userDetails, long currentTime) throws JsonProcessingException {
        return getToken(generateClaims(userDetails), userDetails, currentTime);
    }

    private String getToken(Map<String, Object> claims, UserDetails userDetails, long currentTime) {
        if (claims == null || userDetails == null) {
            throw new IllegalArgumentException("Claims and UserDetails must not be null");
        }
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime + expirationTime))
                .signWith(getKey(), SignatureAlgorithm.HS512)
                .setHeaderParam("typ", "JWT")
                .base64UrlEncodeWith(Encoders.BASE64URL)
                .compact();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public boolean isTokenExpired(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);

            // Verificar si la fecha de expiración es anterior a la fecha actual
            return claims.getBody().getExpiration().before(new Date());
        } catch (JwtException e) {
            // Si hay una excepción, asumimos que el token es inválido o ha expirado
            return true;
        }
    }


    @Override
    public String getUsernameFromToken(String token) {
        Jws<Claims> claimsJws = getAllClaims(token);
        if (Objects.nonNull(claimsJws)) {
            return claimsJws.getBody().getSubject();
        }
        throw new RequestException("The token is invalid.");

    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private Jws<Claims> getAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            return null;
        }

    }

    private Claims generateClaims(UserDetails userDetails) {
        Claims claims = new DefaultClaims();
        claims.put("username", userDetails.getUsername());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();
        claims.put("roles", roles);
        return claims;
    }
}

