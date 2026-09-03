package com.trading.vroom.infrastructure;

import com.trading.vroom.domain.UserAccount;
import com.trading.vroom.ports.AccountRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Repository
@Primary
public class JdbcAccountRepository implements AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserAccount findOrCreate(String userId, BigDecimal initialCash) {
        return findById(userId).orElseGet(() -> {
            BigDecimal normalized = initialCash.setScale(2, RoundingMode.HALF_UP);
            jdbcTemplate.update(
                    "INSERT INTO user_accounts (user_id, cash_balance) VALUES (?, ?) ON CONFLICT (user_id) DO NOTHING",
                    userId,
                    normalized
            );
            return findById(userId).orElse(new UserAccount(userId, normalized));
        });
    }

    @Override
    public Optional<UserAccount> findById(String userId) {
        try {
            UserAccount account = jdbcTemplate.queryForObject(
                    "SELECT cash_balance FROM user_accounts WHERE user_id = ?",
                    (rs, rowNum) -> {
                        BigDecimal cashBalance = rs.getBigDecimal("cash_balance");
                        UserAccount result = new UserAccount(userId, cashBalance);
                        loadPositions(result);
                        return result;
                    },
                    userId
            );
            return Optional.ofNullable(account);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void save(UserAccount account) {
        BigDecimal normalizedCash = account.getCashBalance().setScale(2, RoundingMode.HALF_UP);
        jdbcTemplate.update(
                "INSERT INTO user_accounts (user_id, cash_balance) VALUES (?, ?) " +
                        "ON CONFLICT (user_id) DO UPDATE SET cash_balance = EXCLUDED.cash_balance",
                account.getUserId(),
                normalizedCash
        );

        jdbcTemplate.update("DELETE FROM user_positions WHERE user_id = ?", account.getUserId());
        account.getPositionsSnapshot().forEach((symbol, quantity) ->
                jdbcTemplate.update(
                        "INSERT INTO user_positions (user_id, symbol, quantity) VALUES (?, ?, ?)",
                        account.getUserId(),
                        symbol,
                        quantity
                )
        );
    }

    private void loadPositions(UserAccount account) {
        jdbcTemplate.query(
                "SELECT symbol, quantity FROM user_positions WHERE user_id = ? ORDER BY symbol",
                (rs, rowNum) -> {
                    account.addPosition(rs.getString("symbol"), rs.getBigDecimal("quantity"));
                    return null;
                },
                account.getUserId()
        );
    }
}
