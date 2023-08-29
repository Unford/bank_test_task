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
    CONSTRAINT "uk_bank_accounts_account" UNIQUE ("account"),
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

INSERT INTO "banks" ("name")
VALUES ('Bank A'),
       ('Bank B'),
       ('Bank C'),
       ('Bank D'),
       ('Bank E');

INSERT INTO "users" ("full_name")
VALUES
    ('John Doe'),
    ('Jane Smith'),
    ('Alex Johnson'),
    ('Emily Brown'),
    ('Michael Wilson'),
    ('Emma Davis'),
    ('William Martinez'),
    ('Olivia Thompson'),
    ('James Garcia'),
    ('Sophia Robinson'),
    ('Robert White'),
    ('Ava Lopez'),
    ('David Lee'),
    ('Isabella Hall'),
    ('Daniel Young'),
    ('Mia Harris'),
    ('Joseph Turner'),
    ('Charlotte Scott'),
    ('Thomas Adams'),
    ('Amelia King');

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('1111111111', NOW(), NOW(), 'BYN', 1, 1),
    ('1111111112', NOW(), NOW(), 'BYN', 2, 1);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('2222222221', NOW(), NOW(), 'BYN', 3, 2),
    ('2222222222', NOW(), NOW(), 'BYN', 4, 2);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('3333333331', NOW(), NOW(), 'BYN', 3, 3),
    ('3333333332', NOW(), NOW(), 'BYN', 4, 3);


INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('4444444441', NOW(), NOW(), 'BYN', 5, 4),
    ('4444444442', NOW(), NOW(), 'BYN', 1, 4);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('5555555551', NOW(), NOW(), 'BYN', 2, 5),
    ('5555555552', NOW(), NOW(), 'BYN', 3, 5);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('6666666661', NOW(), NOW(), 'BYN', 4, 6),
    ('6666666662', NOW(), NOW(), 'BYN', 5, 6);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('7777777771', NOW(), NOW(), 'BYN', 1, 7),
    ('7777777772', NOW(), NOW(), 'BYN', 2, 7);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('8888888881', NOW(), NOW(), 'BYN', 3, 8),
    ('8888888882', NOW(), NOW(), 'BYN', 4, 8);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('9999999991', NOW(), NOW(), 'BYN', 5, 9),
    ('9999999992', NOW(), NOW(), 'BYN', 1, 9);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('1010101011', NOW(), NOW(), 'BYN', 2, 10),
    ('1010101012', NOW(), NOW(), 'BYN', 3, 10);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('12111111111', NOW(), NOW(), 'BYN', 4, 11),
    ('12111111112', NOW(), NOW(), 'BYN', 5, 11);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('12121212121', NOW(), NOW(), 'BYN', 1, 12),
    ('12121212122', NOW(), NOW(), 'BYN', 2, 12);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('13131313131', NOW(), NOW(), 'BYN', 1, 13),
    ('13131313132', NOW(), NOW(), 'BYN', 2, 13);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('14141414141', NOW(), NOW(), 'BYN', 3, 14),
    ('14141414142', NOW(), NOW(), 'BYN', 4, 14);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('15151515151', NOW(), NOW(), 'BYN', 5, 15),
    ('15151515152', NOW(), NOW(), 'BYN', 1, 15);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('16161616161', NOW(), NOW(), 'BYN', 2, 16),
    ('16161616162', NOW(), NOW(), 'BYN', 3, 16);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('17171717171', NOW(), NOW(), 'BYN', 4, 17),
    ('17171717172', NOW(), NOW(), 'BYN', 5, 17);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('18181818181', NOW(), NOW(), 'BYN', 1, 18),
    ('18181818182', NOW(), NOW(), 'BYN', 2, 18);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('19191919191', NOW(), NOW(), 'BYN', 3, 19),
    ('19191919192', NOW(), NOW(), 'BYN', 4, 19);


INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "currency", "bank_id", "user_id")
VALUES
    ('3939393939', NOW(), NOW(), 'BYN', 5, 20),
    ('4040404040', NOW(), NOW(), 'BYN', 1, 20);

