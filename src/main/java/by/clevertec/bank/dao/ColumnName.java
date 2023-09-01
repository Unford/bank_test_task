package by.clevertec.bank.dao;


/**
 * The ColumnName class contains constants for column names in a database schema for transactions, accounts, banks, and
 * users.
 */
public final class ColumnName {
    private ColumnName(){}

    /**
     * The type Transaction.
     */
    public static final class Transaction{
        private Transaction(){}

        /**
         * The constant ACCOUNT_TRANSACTION_ID.
         */
        public static final String ACCOUNT_TRANSACTION_ID = "account_transaction_id";
        /**
         * The constant ACCOUNT_TRANSACTION_SUM.
         */
        public static final String ACCOUNT_TRANSACTION_SUM = "sum";
        /**
         * The constant ACCOUNT_TRANSACTION_DATE.
         */
        public static final String ACCOUNT_TRANSACTION_DATE = "date";
        /**
         * The constant ACCOUNT_TRANSACTION_SENDER_ID.
         */
        public static final String ACCOUNT_TRANSACTION_SENDER_ID = "sender_account_id";
        /**
         * The constant ACCOUNT_TRANSACTION_OWNER_ID.
         */
        public static final String ACCOUNT_TRANSACTION_OWNER_ID = "owner_accounts_id";
    }

    /**
     * The type Account.
     */
    public static final class Account{
        private Account(){}

        /**
         * The constant ACCOUNT_ID.
         */
        public static final String ACCOUNT_ID = "bank_account_id";
        /**
         * The constant ACCOUNT_NUM.
         */
        public static final String ACCOUNT_NUM = "account";
        /**
         * The constant OPEN_DATE.
         */
        public static final String OPEN_DATE = "open_date";
        /**
         * The constant LAST_ACCRUAL_DATE.
         */
        public static final String LAST_ACCRUAL_DATE = "last_accrual_date";
        /**
         * The constant BANK_ID.
         */
        public static final String BANK_ID = "bank_id";
        /**
         * The constant USER_ID.
         */
        public static final String USER_ID = "user_id";

        /**
         * The constant OWNER_ACCOUNT.
         */
        public static final String OWNER_ACCOUNT = "owner_account";
        /**
         * The constant SENDER_ACCOUNT.
         */
        public static final String SENDER_ACCOUNT = "sender_account";



    }

    /**
     * The type Bank.
     */
    public static final class Bank{
        private Bank(){}

        /**
         * The constant BANK_ID.
         */
        public static final String BANK_ID = "bank_id";
        /**
         * The constant BANK_NAME.
         */
        public static final String BANK_NAME = "name";
        /**
         * The constant OWNER_BANK_NAME.
         */
        public static final String OWNER_BANK_NAME = "owner_bank";
        /**
         * The constant SENDER_BANK_NAME.
         */
        public static final String SENDER_BANK_NAME = "sender_bank";

    }

    /**
     * The type User.
     */
    public static final class User{
        private User(){}

        /**
         * The constant USER_ID.
         */
        public static final String USER_ID = "user_id";
        /**
         * The constant FULL_NAME.
         */
        public static final String FULL_NAME = "full_name";
        /**
         * The constant OWNER_FULL_NAME.
         */
        public static final String OWNER_FULL_NAME = "owner_full_name";
        /**
         * The constant SENDER_FULL_NAME.
         */
        public static final String SENDER_FULL_NAME = "sender_full_name";


    }

}
