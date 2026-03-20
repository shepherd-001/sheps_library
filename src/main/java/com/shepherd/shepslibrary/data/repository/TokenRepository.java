package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.TokenType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<ShepsToken, String> {
    @Query("select t from ShepsToken t where t.token = :token or t.refreshToken = :token and t.tokenType = :tokenType")
    Optional<ShepsToken> findByTokenAndTokenType(String token, @Param("tokenType")TokenType tokenType);

    Optional<ShepsToken> findShepsTokenByTokenAndTokenType(String token, TokenType tokenType);

    @Modifying
    @Transactional
    @Query("update ShepsToken t set t.isRevoked = true, t.isExpired = true where t.token = :token and t.tokenType = :tokenType")
    int revokeToken(@Param("token") String token, @Param("tokenType") TokenType tokenType);

    @Modifying
    @Transactional
    @Query("update ShepsToken t set t.isRevoked = true, t.isExpired = true where t.user.email = :email and t.tokenType = :tokenType")
    int revokeAllTokensForUser(@Param("email") String email, @Param("tokenType") TokenType tokenType);

    @Modifying
    @Transactional
    @Query("delete from ShepsToken t where (t.isRevoked = true or t.isExpired = true) and t.createdAt < :cutoff")
    int deleteAllRevokedOrExpiredTokensOlderThan(@Param("cutoff") Instant cutoff);
}