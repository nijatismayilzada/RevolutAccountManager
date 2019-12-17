package com.revolut.revolutaccountmanager.service;

import com.revolut.revolutaccountmanager.model.request.TransactionRequest;
import com.revolut.revolutaccountmanager.model.transaction.Transaction;
import com.revolut.revolutaccountmanager.model.transaction.TransactionType;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

@Service
public class TransactionManagerClient {

    private static final String GET_TRANSACTION_URI
            = "http://localhost:8082/transactions/transaction-id";
    private static final String POST_TRANSACTION_REQUEST_URI
            = "http://localhost:8082/transactions/create";

    private Client client;

    @Inject
    public TransactionManagerClient() {
        client = ClientBuilder.newClient();
    }

    public Transaction getTransactionById(long transactionId) {
        return client.target(GET_TRANSACTION_URI)
                .path(String.valueOf(transactionId))
                .request(MediaType.APPLICATION_JSON)
                .get(Transaction.class);
    }

    public void createSimpleIncreaseTransactionRequest(Transaction referenceTransaction) {

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAccountId(Integer.parseInt(referenceTransaction.getReference()));
        transactionRequest.setAmount(referenceTransaction.getAmount());
        transactionRequest.setCurrency(referenceTransaction.getCurrency());
        transactionRequest.setReference(String.valueOf(referenceTransaction.getAccountId()));
        transactionRequest.setTransactionType(TransactionType.REVOLUT_SIMPLE_INCREASE);

        client.target(POST_TRANSACTION_REQUEST_URI)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(transactionRequest, MediaType.APPLICATION_JSON));
    }
}
