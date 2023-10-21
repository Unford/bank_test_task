CREATE DATABASE bank;


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
        VARCHAR(45) NOT NULL,
    PRIMARY KEY
        (
         "bank_id"
            ),
    CONSTRAINT "name_UNIQUE" UNIQUE
        (
         "name"
            )
)
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
        VARCHAR(95) NOT NULL,
    PRIMARY KEY
        (
         "user_id"
            )
)
;

CREATE TABLE IF NOT EXISTS "bank_accounts"
(
    "bank_account_id"
                        BIGSERIAL
                                    NOT
                                        NULL,
    "account"
                        VARCHAR(45) NOT NULL,
    "open_date"         DATE        NOT NULL,
    "last_accrual_date" DATE        NOT NULL,
    "bank_id"           BIGINT      NOT NULL,
    "user_id"           BIGINT      NOT NULL,
    PRIMARY KEY
        (
         "bank_account_id"
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
            ON UPDATE NO ACTION
)
;
CREATE TABLE IF NOT EXISTS "accounts_transactions"
(
    "account_transaction_id"
                        BIGSERIAL
                                  NOT
                                      NULL,
    "sum"
                        NUMERIC(10,
                            2)    NOT NULL,
    "date"              TIMESTAMP NOT NULL,
    "sender_account_id" BIGINT,
    "owner_accounts_id" BIGINT    NOT NULL,
    PRIMARY KEY
        (
         "account_transaction_id"
            ),
    CONSTRAINT "fk_accounts_transactions_bank_accounts2"
        FOREIGN KEY
            (
             "sender_account_id"
                )
            REFERENCES "bank_accounts"
                (
                 "bank_account_id"
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
                 "bank_account_id"
                    )
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
)
;

INSERT INTO "banks" ("name")
VALUES ('Bank A'),
       ('Bank B'),
       ('Bank C'),
       ('Bank D'),
       ('Bank E');

INSERT INTO "users" ("full_name")
VALUES ('John Doe'),
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

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('1111111111', NOW(), NOW(), 1, 1),
       ('1111111112', NOW(), NOW(), 2, 1);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('2222222221', NOW(), NOW(), 3, 2),
       ('2222222222', NOW(), NOW(), 4, 2);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('3333333331', NOW(), NOW(), 3, 3),
       ('3333333332', NOW(), NOW(), 4, 3);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('4444444441', NOW(), NOW(), 5, 4),
       ('4444444442', '2023-08-10', '2023-08-10', 1, 4);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('5555555551', NOW(), NOW(), 2, 5),
       ('5555555552', NOW(), NOW(), 3, 5);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('6666666661', NOW(), NOW(), 4, 6),
       ('6666666662', '2023-09-10', '2023-09-10', 5, 6);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('7777777771', NOW(), NOW(), 1, 7),
       ('7777777772', NOW(), NOW(), 2, 7);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('8888888881', NOW(), NOW(), 3, 8),
       ('8888888882', '2023-09-10', '2023-09-10', 4, 8);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('9999999991', NOW(), NOW(), 5, 9),
       ('9999999992', NOW(), NOW(), 1, 9);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('1010101011', NOW(), NOW(), 2, 10),
       ('1010101012', NOW(), NOW(), 3, 10);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('12111111111', NOW(), NOW(), 4, 11),
       ('12111111112', NOW(), NOW(), 5, 11);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('12121212121', NOW(), NOW(), 1, 12),
       ('12121212122', NOW(), NOW(), 2, 12);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('13131313131', NOW(), NOW(), 1, 13),
       ('13131313132', NOW(), NOW(), 2, 13);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('14141414141', NOW(), NOW(), 3, 14),
       ('14141414142', NOW(), NOW(), 4, 14);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('15151515151', NOW(), NOW(), 5, 15),
       ('15151515152', NOW(), NOW(), 1, 15);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('16161616161', NOW(), NOW(), 2, 16),
       ('16161616162', NOW(), NOW(), 3, 16);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('17171717171', NOW(), NOW(), 4, 17),
       ('17171717172', NOW(), NOW(), 5, 17);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('18181818181', NOW(), NOW(), 1, 18),
       ('18181818182', NOW(), NOW(), 2, 18);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('19191919191', NOW(), NOW(), 3, 19),
       ('19191919192', NOW(), NOW(), 4, 19);

INSERT INTO "bank_accounts" ("account", "open_date", "last_accrual_date", "bank_id", "user_id")
VALUES ('3939393939', NOW(), NOW(), 5, 20),
       ('4040404040', NOW(), NOW(), 1, 20);


INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (100.00, '2023-08-15 10:30:00', 1),
       (-50.00, '2023-08-16 14:45:00', 1),
       (25.00, '2023-08-17 09:15:00', 1),
       (-10.00, '2023-08-18 16:20:00', 1);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (500.00, '2023-08-15 11:00:00', 2),
       (-100.00, '2023-08-16 15:30:00', 2),
       (200.00, '2023-08-17 10:45:00', 2),
       (-50.00, '2023-08-18 17:00:00', 2);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (1000.00, '2023-08-15 11:30:00', 3),
       (-300.00, '2023-08-16 16:15:00', 3),
       (400.00, '2023-08-17 11:30:00', 3),
       (-200.00, '2023-08-18 18:00:00', 3);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (200.00, '2023-08-15 12:00:00', 4),
       (-50.00, '2023-08-16 17:00:00', 4),
       (100.00, '2023-08-17 12:15:00', 4),
       (-30.00, '2023-08-18 19:30:00', 4);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (300.00, '2023-08-15 12:30:00', 5),
       (-100.00, '2023-08-16 17:45:00', 5),
       (150.00, '2023-08-17 13:00:00', 5),
       (-40.00, '2023-08-18 20:15:00', 5);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (400.00, '2023-08-15 13:00:00', 6),
       (-200.00, '2023-08-16 18:30:00', 6),
       (300.00, '2023-08-17 13:45:00', 6),
       (-100.00, '2023-08-18 21:00:00', 6);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (800.00, '2023-08-15 13:30:00', 7),
       (-400.00, '2023-08-16 19:00:00', 7),
       (600.00, '2023-08-17 14:00:00', 7),
       (-200.00, '2023-08-18 21:45:00', 7);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (1200.00, '2023-08-15 14:00:00', 8),
       (-600.00, '2023-08-16 19:45:00', 8),
       (900.00, '2023-08-17 14:30:00', 8),
       (-300.00, '2023-08-18 22:30:00', 8);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (1500.00, '2023-08-15 14:30:00', 9),
       (-750.00, '2023-08-16 20:15:00', 9),
       (1200.00, '2023-08-17 15:00:00', 9),
       (-600.00, '2023-08-18 23:15:00', 9);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (2000.00, '2023-08-15 15:00:00', 10),
       (-1000.00, '2023-08-16 21:00:00', 10),
       (1800.00, '2023-08-17 15:30:00', 10),
       (-900.00, '2023-08-19 00:00:00', 10);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (2500.00, '2023-08-15 15:30:00', 11),
       (-1250.00, '2023-08-16 22:00:00', 11),
       (2200.00, '2023-08-17 16:00:00', 11),
       (-1100.00, '2023-08-19 00:45:00', 11);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (3000.00, '2023-08-15 16:00:00', 12),
       (-1500.00, '2023-08-16 22:45:00', 12),
       (2600.00, '2023-08-17 16:30:00', 12),
       (-1300.00, '2023-08-19 01:30:00', 12);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (3500.00, '2023-08-15 16:30:00', 13),
       (-1750.00, '2023-08-17 23:15:00', 13),
       (3000.00, '2023-08-18 00:00:00', 13),
       (-1500.00, '2023-08-19 02:15:00', 13);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (4000.00, '2023-08-15 17:00:00', 14),
       (-2000.00, '2023-08-18 01:30:00', 14),
       (3400.00, '2023-08-18 02:15:00', 14),
       (-1700.00, '2023-08-19 03:00:00', 14);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (4500.00, '2023-08-15 17:30:00', 15),
       (-2250.00, '2023-08-18 03:45:00', 15),
       (3800.00, '2023-08-18 04:30:00', 15),
       (-1900.00, '2023-08-19 04:45:00', 15);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (5000.00, '2023-08-15 18:00:00', 16),
       (-2500.00, '2023-08-18 05:00:00', 16),
       (4200.00, '2023-08-18 06:15:00', 16),
       (-2100.00, '2023-08-19 06:30:00', 16);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (5500.00, '2023-08-15 18:30:00', 17),
       (-2750.00, '2023-08-18 07:00:00', 17),
       (4600.00, '2023-08-18 08:30:00', 17),
       (-2300.00, '2023-08-19 09:15:00', 17);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (6000.00, '2023-08-15 19:00:00', 18),
       (-3000.00, '2023-08-18 09:45:00', 18),
       (5000.00, '2023-08-18 10:15:00', 18),
       (-2500.00, '2023-08-19 10:45:00', 18);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (6500.00, '2023-08-15 19:30:00', 19),
       (-3250.00, '2023-08-18 11:00:00', 19),
       (5400.00, '2023-08-18 11:30:00', 19),
       (-2700.00, '2023-08-19 12:00:00', 19);

INSERT INTO "accounts_transactions" ("sum", "date", "owner_accounts_id")
VALUES (7000.00, '2023-08-15 20:00:00', 20),
       (-3500.00, '2023-08-18 12:30:00', 20),
       (5800.00, '2023-08-18 13:00:00', 20),
       (-2900.00, '2023-08-19 13:30:00', 20);
