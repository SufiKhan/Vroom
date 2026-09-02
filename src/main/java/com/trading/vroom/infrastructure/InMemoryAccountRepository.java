package com.example.trading.vroom.infrastructure;

import com.example.trading.vroom.domain.UserAccount;
import com.example.trading.vroom.ports.AccountRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryAccountRepository implements AccountRepository {

    private final ConcurrentMap<String, UserAccount> accounts = new ConcurrentHashMap<>();

    @Override
    public UserAccount findOrCreate(String userId, BigDecimal initialCash) {
        return accounts.computeIfAbsent(userId, key -> new UserAccount(key, initialCash));
    }

    @Override
    public Optional<UserAccount> findById(String userId) {
        return Optional.ofNullable(accounts.get(userId));
    }

    @Override
    public void save(UserAccount account) {
        accounts.put(account.getUserId(), account);
    }
}
