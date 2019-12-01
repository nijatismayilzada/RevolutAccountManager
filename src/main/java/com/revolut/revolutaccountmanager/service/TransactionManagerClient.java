package com.revolut.revolutaccountmanager.service;

import com.revolut.revolutaccountmanager.model.transaction.Transaction;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

@Service
public class TransactionManagerClient {

    private static final String REST_URI
            = "http://localhost:8082/transactions/transaction-id";

    private Client client;

    @Inject
    public TransactionManagerClient() {
        client = ClientBuilder.newClient();
    }

    public Transaction getTransactionById(long transactionId) {
        return client.target(REST_URI)
                .path(String.valueOf(transactionId))
                .request(MediaType.APPLICATION_JSON)
                .get(Transaction.class);
    }
}
