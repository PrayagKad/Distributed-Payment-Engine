-- V2__create_transactions.sql
-- Creates the transactions table.
--
-- Transactions are APPEND-ONLY — never updated or deleted.
-- This is both a regulatory requirement and a practical audit trail.
-- Every balance change must be permanently traceable.
--
-- For a transfer from A → B, two rows are inserted:
--   row 1: account_id = A, type = TRANSFER_DEBIT,  amount = X
--   row 2: account_id = B, type = TRANSFER_CREDIT, amount = X
-- Both rows are inserted in the same DB transaction (@Transactional).

CREATE TABLE IF NOT EXISTS transactions (
                                            id                       BIGSERIAL      PRIMARY KEY,
                                            account_id               BIGINT         NOT NULL REFERENCES accounts(id),
    type                     VARCHAR(20)    NOT NULL
    CHECK (type IN ('DEPOSIT','WITHDRAWAL','TRANSFER_DEBIT','TRANSFER_CREDIT')),
    amount                   NUMERIC(19,4)  NOT NULL CHECK (amount > 0),
    balance_after            NUMERIC(19,4)  NOT NULL,
    reference_account_number VARCHAR(20),
    description              VARCHAR(255),
    created_at               TIMESTAMP      NOT NULL DEFAULT NOW()
    );

-- Critical index: every history query filters by account_id
CREATE INDEX idx_transactions_account_id ON transactions (account_id, created_at DESC);

COMMENT ON TABLE  transactions                         IS 'Immutable ledger of all balance changes. Never update or delete rows.';
COMMENT ON COLUMN transactions.balance_after           IS 'Account balance immediately after this transaction — enables statement generation without replaying history';
COMMENT ON COLUMN transactions.reference_account_number IS 'For transfers: the other account involved in the transfer';