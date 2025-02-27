package com.scaler.userservice.repositories;

import com.scaler.userservice.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Token save(Token token);

    Optional<Token> findByValueAndDeletedAndExpiryGreaterThan(String token, Boolean deleted, Date expiry);

    Optional<Token> findByValueAndDeleted(String token, Boolean deleted);
}
