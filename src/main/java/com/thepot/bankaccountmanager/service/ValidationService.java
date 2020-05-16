package com.thepot.bankaccountmanager.service;

import com.thepot.bankaccountmanager.model.account.Account;
import com.thepot.bankaccountmanager.model.exception.TransactionRuntimeException;
import com.thepot.bankaccountmanager.model.transaction.Transaction;
import com.thepot.bankaccountmanager.model.transaction.TransactionType;
import org.jvnet.hk2.annotations.Service;

import java.math.BigDecimal;

@Service
public class ValidationService {
    public void validateTransaction(Account account, Transaction transaction) {
        if (account.getAccountId() == 0) {
            throw new TransactionRuntimeException(String.format("Could not find account. account: %s, transaction %s", account, transaction));
        }

        if (!account.getCurrency().equals(transaction.getCurrency())) {
            throw new TransactionRuntimeException(String.format("Different currency transactions are not supported yet. account: %s, transaction %s", account, transaction));
        }

        if (transaction.getTransactionType() == TransactionType.SIMPLE_DECREASE || transaction.getTransactionType() == TransactionType.TRANSFER) {
            if (transaction.getAmount().compareTo(account.getBalance()) > 0) {
                throw new TransactionRuntimeException(String.format("Not enough funds. account: %s, transaction %s", account, transaction));
            }
        }

        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionRuntimeException(String.format("Negative transaction? account: %s, transaction %s", account, transaction));
        }
    }
}
