CREATE TABLE IF NOT EXISTS user_accounts (
    user_id VARCHAR(255) PRIMARY KEY,
    cash_balance NUMERIC(19, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_positions (
    user_id VARCHAR(255) NOT NULL,
    symbol VARCHAR(64) NOT NULL,
    quantity NUMERIC(19, 4) NOT NULL,
    PRIMARY KEY (user_id, symbol)
);
