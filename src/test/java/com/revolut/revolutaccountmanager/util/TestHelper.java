package com.revolut.revolutaccountmanager.util;

import com.revolut.revolutaccountmanager.model.account.Account;
import com.revolut.revolutaccountmanager.model.transaction.Transaction;
import com.revolut.revolutaccountmanager.model.transaction.TransactionAction;
import com.revolut.revolutaccountmanager.model.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.Currency;

public class TestHelper {

    public static Transaction getTransaction(long transactionId, long accountId, BigDecimal amount, Currency currency, TransactionAction transactionAction) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setTransactionAction(transactionAction);
        transaction.setTransactionType(TransactionType.REVOLUT_SIMPLE);
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
