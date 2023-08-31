package by.clevertec.bank.dao;

public final class ColumnName {
    private ColumnName(){}

    public static final class Transaction{
        private Transaction(){}
        public static final String ACCOUNT_TRANSACTION_ID = "account_transaction_id";
        public static final String ACCOUNT_TRANSACTION_SUM = "sum";
        public static final String ACCOUNT_TRANSACTION_DATE = "date";
        public static final String ACCOUNT_TRANSACTION_SENDER_ID = "sender_account_id";
        public static final String ACCOUNT_TRANSACTION_OWNER_ID = "owner_accounts_id";
    }

    public static final class Account{
        private Account(){}
        public static final String ACCOUNT_ID = "bank_account_id";
        public static final String ACCOUNT_NUM = "account";
        public static final String OPEN_DATE = "open_date";
        public static final String LAST_ACCRUAL_DATE = "last_accrual_date";
        public static final String BANK_ID = "bank_id";
        public static final String USER_ID = "user_id";

        public static final String OWNER_ACCOUNT = "owner_account";
        public static final String SENDER_ACCOUNT = "sender_account";



    }

    public static final class Bank{
        private Bank(){}
        public static final String BANK_ID = "bank_id";
        public static final String BANK_NAME = "name";
        public static final String OWNER_BANK_NAME = "owner_bank";
        public static final String SENDER_BANK_NAME = "sender_bank";

    }

    public static final class User{
        private User(){}
        public static final String USER_ID = "user_id";
        public static final String FULL_NAME = "full_name";
        public static final String OWNER_FULL_NAME = "owner_full_name";
        public static final String SENDER_FULL_NAME = "sender_full_name";


    }

}
