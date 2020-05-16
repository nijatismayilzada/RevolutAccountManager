package com.thepot.bankaccountmanager.model.request;

import java.util.Currency;
import java.util.Objects;

public class CreateAccountRequest {
    private long userId;
    private Currency currency;

    public CreateAccountRequest() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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
        CreateAccountRequest that = (CreateAccountRequest) o;
        return userId == that.userId &&
                Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, currency);
    }

    @Override
    public String toString() {
        return "CreateAccountRequest{" +
                "userId=" + userId +
                ", currency=" + currency +
                '}';
    }
}
