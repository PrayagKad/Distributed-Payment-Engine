-- V1__create_accounts.sql
-- Creates the accounts table.
--
-- NUMERIC(19,4): exact decimal — never FLOAT for money.
-- 19 total digits, 4 decimal places — covers any real-world currency amount.
--
-- version column: used by Hibernate @Version for optimistic locking.
-- Every UPDATE increments this. If two transactions both read version=1
-- and try to save, one succeeds (sets version=2), the other gets an error.

CREATE TABLE IF NOT EXISTS accounts (
                                        id             BIGSERIAL       PRIMARY KEY,
                                        account_number VARCHAR(20)     NOT NULL UNIQUE,
    owner_name     VARCHAR(100)    NOT NULL,
    email          VARCHAR(100)    NOT NULL UNIQUE,
    balance        NUMERIC(19, 4)  NOT NULL DEFAULT 0.0000 CHECK (balance >= 0),
    account_type   VARCHAR(20)     NOT NULL CHECK (account_type IN ('SAVINGS', 'CURRENT')),
    active         BOOLEAN         NOT NULL DEFAULT TRUE,
    version        BIGINT          NOT NULL DEFAULT 0,
    created_at     TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP       NOT NULL DEFAULT NOW()
    );

CREATE INDEX idx_accounts_account_number ON accounts (account_number);
CREATE INDEX idx_accounts_email          ON accounts (email);

COMMENT ON TABLE  accounts                IS 'Bank accounts — soft delete via active flag, never hard delete';
COMMENT ON COLUMN accounts.balance        IS 'Current balance in NUMERIC(19,4). Always use BigDecimal in Java, never double.';
COMMENT ON COLUMN accounts.version        IS 'Optimistic lock version — Hibernate increments on every UPDATE';
COMMENT ON COLUMN accounts.account_number IS 'Auto-generated 12-digit unique account number';