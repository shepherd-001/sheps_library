package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<ShepsToken, UUID> {
    @Query("""
           select t from ShepsToken t
           where t.user.email = :email and t.token = :token
           and t.tokenType = :tokenType\s
          \s""")
    ShepsToken findByUserAndTokenAndTokenType(@Param("email") String email,
         @Param("token") String token, @Param("tokenType") TokenType tokenType);
    Optional<ShepsToken> findByTokenAndTokenType(String token, TokenType tokenType);
    List<ShepsToken> findAllByUserIdAndTokenType(UUID userId, TokenType tokenType);
}
