package com.thepot.bankaccountmanager.util;

import com.thepot.bankaccountmanager.model.account.Account;
import com.thepot.bankaccountmanager.model.transaction.Transaction;
import com.thepot.bankaccountmanager.model.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.Currency;

public class TestHelper {

    public static Transaction getTransaction(long transactionId, long accountId, BigDecimal amount, Currency currency) {
        return getTransaction(transactionId, accountId, amount, currency, TransactionType.SIMPLE_INCREASE);
    }

    public static Transaction getTransaction(long transactionId, long accountId, BigDecimal amount, Currency currency, TransactionType transactionType) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setTransactionType(transactionType);
        return transaction;
    }


    public static Account getAccount(long accountId) {
        return getAccount(accountId, BigDecimal.ZERO, Currency.getInstance("GBP"));
    }

    public static Account getAccount(long accountId, BigDecimal balance, Currency currency) {
        Account account = new Account();
        account.setAccountId(accountId);
        account.setBalance(balance);
        account.setCurrency(currency);
        return account;
    }
}
