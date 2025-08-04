package com.learnspherel.utils;

import com.learnspherel.exception.InvalidJwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationInMs;
    private MessageSource messageSource;

    public JwtUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("vaiTro", userDetails.getAuthorities())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);  // Extract username from token.
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));  // Validate token.
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);  // Extract the subject (username).
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);  // Extract all claims.
        return claimsResolver.apply(claims);  // Apply the claims resolver.
    }

    private Claims extractAllClaims(String token) {
        try {
            JwtParser parser = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build();
            Jws<Claims> jws = parser.parseSignedClaims(token);
            return jws.getPayload();
        } catch (
                ExpiredJwtException e) {
            throw new InvalidJwtException(messageSource.getMessage("auth.jwt.expired", null, "Jwt token hết hạn !", null), e);
        } catch (MalformedJwtException e) {
            throw new InvalidJwtException(messageSource.getMessage("auth.jwt.malformed", null, "Định dạng Jwt token không hợp lệ !", null), e);
        } catch (SignatureException e) {
            throw new InvalidJwtException(messageSource.getMessage("auth.jwt.signature", null, "JWT signature không hợp lệ !", null), e);
        } catch (Exception e) {
            throw new InvalidJwtException("Jwt token không hợp lệ !", e);
        }
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());  // Check if the token has expired.
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);  // Extract the expiration date.
    }
}