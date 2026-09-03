package com.trading.vroom.ports;

import com.trading.vroom.domain.UserAccount;

import java.math.BigDecimal;
import java.util.Optional;

public interface WalletRepository {
    UserAccount findOrCreate(String userId, BigDecimal initialCash);

    Optional<UserAccount> findById(String userId);

    void save(UserAccount account);
}
