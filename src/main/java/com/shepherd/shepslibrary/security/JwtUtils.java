package com.shepherd.shepslibrary.security;

import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.TokenRepository;
import com.shepherd.shepslibrary.common.exceptions.InvalidJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {
    private final SecretKey signingKey;
    private final TokenRepository tokenRepository;
    private JwtParser jwtParser;
    private static final String ISSUER = "shep_library";
    private static final String TOKEN_TYPE = "type";
    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";
    private static final String TOKEN_VERSION = "version";

    @PostConstruct
    void init() {
        this.jwtParser = Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(ISSUER)
                .build();
    }

    public String extractUsername(String jwtToken){
        return extractClaim(jwtToken, Claims::getSubject);
    }

    private <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken) {
        try{
            return jwtParser.parseSignedClaims(jwtToken).getPayload();
        }catch (JwtException ex){
            log.error(ex.getMessage());
            throw new InvalidJwtException("Invalid or expired token");
        }
    }

    public String generateAccessToken(User user, long accessTokenExpiration){
        return buildJwtToken(user, ACCESS, accessTokenExpiration);
    }

    public String generateRefreshToken(User user, long refreshTokenExpiration){
        return buildJwtToken(user, REFRESH, refreshTokenExpiration);
    }

    private String buildJwtToken(User user, String tokenType, long tokenExpiration){
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE, tokenType);
        claims.put("authority", user.getRole().getName());
        claims.put(TOKEN_VERSION, user.getTokenVersion()); // critical for revocation
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .issuer(ISSUER)
                .id(UUID.randomUUID().toString())
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(tokenExpiration)))
                .signWith(signingKey)
                .compact();
    }

    public Instant getExpiration(String token) {
        return extractAllClaims(token).getExpiration().toInstant();
    }

    public boolean isValidToken(String token, String email){
        Claims claims = extractAllClaims(token);
        Instant expiration = claims.getExpiration().toInstant();
        Instant now = Instant.now();

        String subject = claims.getSubject();
        Integer tokenVersion = claims.get(TOKEN_VERSION, Integer.class);
        Integer fetchedTokenVersion = tokenRepository.findTokenVersionByEmail(email);
        return subject != null
                && subject.equalsIgnoreCase(email)
                && expiration.isAfter(now)
                && tokenVersion != null
                && tokenVersion.equals(fetchedTokenVersion);
    }

    public boolean isValidToken(String token){
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().toInstant().isAfter(Instant.now());
    }

    public void validateRefreshToken(String refreshToken){
        Claims claims = extractAllClaims(refreshToken);

        String tokenType = claims.get(TOKEN_TYPE, String.class);
        if(!REFRESH.equals(tokenType)){
            throw new InvalidJwtException("Invalid refresh token");
        }
    }
}