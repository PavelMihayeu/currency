CREATE SCHEMA IF NOT EXISTS currencyapi;

CREATE TABLE IF NOT EXISTS currency (
    code VARCHAR(3) PRIMARY KEY,
    symbol VARCHAR(10),
    name VARCHAR(255),
    symbol_native VARCHAR(10),
    decimal_digits INT,
    rounding INT,
    name_plural VARCHAR(255),
    type VARCHAR(50),
    last_updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS currency_rate (
    id SERIAL PRIMARY KEY,
    currency_code VARCHAR(3) REFERENCES currency(code),
    code VARCHAR(3),
    value DECIMAL(18, 4)
);