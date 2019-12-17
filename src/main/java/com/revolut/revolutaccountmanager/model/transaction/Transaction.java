package com.revolut.revolutaccountmanager.model.transaction;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.revolut.revolutaccountmanager.config.InstantDeserializer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Objects;

public class Transaction {
    private long transactionId;
    private long accountId;
    private String reference;
    private TransactionState transactionState;
    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant stateUpdatedAt;
    private TransactionType transactionType;
    private BigDecimal amount;
    private Currency currency;

    public Transaction() {
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
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

    public TransactionState getTransactionState() {
        return transactionState;
    }

    public void setTransactionState(TransactionState transactionState) {
        this.transactionState = transactionState;
    }

    public Instant getStateUpdatedAt() {
        return stateUpdatedAt;
    }

    public void setStateUpdatedAt(Instant stateUpdatedAt) {
        this.stateUpdatedAt = stateUpdatedAt;
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
        Transaction that = (Transaction) o;
        return transactionId == that.transactionId &&
                accountId == that.accountId &&
                Objects.equals(reference, that.reference) &&
                transactionState == that.transactionState &&
                Objects.equals(stateUpdatedAt, that.stateUpdatedAt) &&
                transactionType == that.transactionType &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, accountId, reference, transactionState, stateUpdatedAt, transactionType, amount, currency);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", accountId=" + accountId +
                ", reference='" + reference + '\'' +
                ", transactionState=" + transactionState +
                ", stateUpdatedAt=" + stateUpdatedAt +
                ", transactionType=" + transactionType +
                ", amount=" + amount +
                ", currency=" + currency +
                '}';
    }
}
