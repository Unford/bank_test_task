CREATE DATABASE bank;
\c bank;


-- -----------------------------------------------------
-- Table "bank"."banks"
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS "banks"
(
    "bank_id"
    BIGSERIAL
    NOT
    NULL,
    "name"
    VARCHAR
(
    45
) NOT NULL,
    PRIMARY KEY
(
    "bank_id"
),
    CONSTRAINT "name_UNIQUE" UNIQUE
(
    "name"
))
;

-- -----------------------------------------------------
-- Table "bank"."users"
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS "users"
(
    "user_id"
    BIGSERIAL
    NOT
    NULL,
    "full_name"
    VARCHAR
(
    95
) NOT NULL,
    PRIMARY KEY
(
    "user_id"
))
;

CREATE TABLE IF NOT EXISTS "bank_accounts"
(
    "id"
    BIGSERIAL
    NOT
    NULL,
    "account"
    VARCHAR
(
    45
) NOT NULL,
    "open_date" TIMESTAMPTZ NOT NULL,
    "last_accrual_date" TIMESTAMPTZ NOT NULL,
    "currency" VARCHAR
(
    10
) NOT NULL,
    "bank_id" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL,
    PRIMARY KEY
(
    "id"
),
    CONSTRAINT "fk_bank_accounts_banks"
    FOREIGN KEY
(
    "bank_id"
)
    REFERENCES "banks"
(
    "bank_id"
)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT "fk_bank_accounts_users1"
    FOREIGN KEY
(
    "user_id"
)
    REFERENCES "users"
(
    "user_id"
)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;
CREATE TABLE IF NOT EXISTS "accounts_transactions"
(
    "id"
    BIGSERIAL
    NOT
    NULL,
    "sum"
    NUMERIC
(
    10,
    2
) NOT NULL,
    "date" TIMESTAMPTZ NOT NULL,
    "sender_account_id" BIGINT NOT NULL,
    "owner_accounts_id" BIGINT NOT NULL,
    PRIMARY KEY
(
    "id"
),
    CONSTRAINT "fk_accounts_transactions_bank_accounts2"
    FOREIGN KEY
(
    "sender_account_id"
)
    REFERENCES "bank_accounts"
(
    "id"
)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT "fk_accounts_transactions_bank_accounts1"
    FOREIGN KEY
(
    "owner_accounts_id"
)
    REFERENCES "bank_accounts"
(
    "id"
)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;