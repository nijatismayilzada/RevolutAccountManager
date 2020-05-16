package com.thepot.bankaccountmanager.model.transaction;

import java.util.Objects;

public class TransactionEvent {
    private long transactionId;

    public TransactionEvent() {
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionEvent that = (TransactionEvent) o;
        return transactionId == that.transactionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return "TransactionEvent{" +
                "transactionId=" + transactionId +
                '}';
    }
}
