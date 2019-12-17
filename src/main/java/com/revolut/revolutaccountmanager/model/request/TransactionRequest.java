package com.revolut.revolutaccountmanager.model.request;


import com.revolut.revolutaccountmanager.model.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public class TransactionRequest {
    private long accountId;
    private String reference;
    private TransactionType transactionType;
    private BigDecimal amount;
    private Currency currency;

    public TransactionRequest() {
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionRequest that = (TransactionRequest) o;
        return accountId == that.accountId &&
                Objects.equals(reference, that.reference) &&
                transactionType == that.transactionType &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, reference, transactionType, amount, currency);
    }

    @Override
    public String toString() {
        return "TransactionRequest{" +
                "accountId=" + accountId +
                ", reference='" + reference + '\'' +
                ", transactionType=" + transactionType +
                ", amount=" + amount +
                ", currency=" + currency +
                '}';
    }
}
