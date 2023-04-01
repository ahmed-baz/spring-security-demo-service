package com.demo.skyros.security.repo;

import com.demo.skyros.security.model.TokenInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenInfoRepo extends JpaRepository<TokenInfo, Long> {

    TokenInfo findByRefreshToken(String refreshToken);

}
