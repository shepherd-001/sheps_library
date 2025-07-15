package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<ShepsToken, String> {
//    @Query("""
//           select t from ShepsToken t
//           where t.user.email = :email and t.token = :token
//           and t.tokenType = :tokenType\s
//          \s""")
//    ShepsToken findByUserAndTokenAndTokenType(@Param("email") String email,
//         @Param("token") String token, @Param("tokenType") TokenType tokenType);
    Optional<ShepsToken> findByTokenAndTokenType(String token, TokenType tokenType);
//    List<ShepsToken> findAllByUserIdAndTokenType(String userId, TokenType tokenType);
    void deleteAllByUserEmailAndTokenType(String email, TokenType tokenType);
}
