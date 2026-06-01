package com.bandsync.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    public String generarToken(String correo, String rol, String artistaId, String bandaId) {
        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .claim("artistaId", artistaId)
                .claim("bandaId", bandaId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validarToken(String token) {
        try {
            parsearClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.warn("Token JWT expirado: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.warn("Token JWT malformado: {}", ex.getMessage());
        } catch (SignatureException ex) {
            log.warn("Firma JWT inválida: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("Token JWT vacío o nulo: {}", ex.getMessage());
        }
        return false;
    }

    public String extraerCorreo(String token) {
        return parsearClaims(token).getSubject();
    }

    public String extraerRol(String token) {
        return parsearClaims(token).get("rol", String.class);
    }

    public String extraerArtistaId(String token) {
        return parsearClaims(token).get("artistaId", String.class);
    }

    public String extraerBandaId(String token) {
        return parsearClaims(token).get("bandaId", String.class);
    }

    private Claims parsearClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
