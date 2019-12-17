package com.revolut.revolutaccountmanager.model.exception;

public class TransactionRuntimeException extends RuntimeException {
    public TransactionRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionRuntimeException(String message) {
        super(message);
    }

    public TransactionRuntimeException(Throwable cause) {
        super(cause);
    }
}
