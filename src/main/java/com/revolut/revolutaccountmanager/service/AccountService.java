package com.revolut.revolutaccountmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.revolutaccountmanager.model.account.Account;
import com.revolut.revolutaccountmanager.model.exception.TransactionRuntimeException;
import com.revolut.revolutaccountmanager.model.transaction.Transaction;
import com.revolut.revolutaccountmanager.model.transaction.TransactionAction;
import com.revolut.revolutaccountmanager.model.transaction.TransactionEvent;
import com.revolut.revolutaccountmanager.model.transaction.TransactionType;
import com.revolut.revolutaccountmanager.repository.AccountRepository;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Service
public class AccountService implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;
    private final MessageConsumer messageConsumer;
    private final TransactionManagerClient transactionManagerClient;
    private final MessageProducer messageProducer;
    private final ValidationService validationService;

    @Inject
    public AccountService(AccountRepository accountRepository, MessageConsumer messageConsumer, TransactionManagerClient transactionManagerClient, MessageProducer messageProducer, ValidationService validationService) throws JMSException {
        this.accountRepository = accountRepository;
        this.messageConsumer = messageConsumer;
        messageConsumer.getConsumer().setMessageListener(this);
        this.transactionManagerClient = transactionManagerClient;
        this.messageProducer = messageProducer;
        this.validationService = validationService;
    }

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            TransactionEvent transactionEvent = new ObjectMapper().readValue(textMessage.getText(), TransactionEvent.class);

            Transaction transaction = transactionManagerClient.getTransactionById(transactionEvent.getTransactionId());


            processTransaction(transaction);

            messageProducer.getTransactionCompletedProducer().send(message);

        } catch (Exception e) {
            handleProcessFailure(message, e);
        }

    }

    private void handleProcessFailure(Message message, Exception e) {
        try {
            LOG.error("Failed to process message: {}", message, e);
            messageProducer.getTransactionFailedProducer().send(message);
        } catch (JMSException ex) {
            throw new TransactionRuntimeException(ex);
        }
    }

    private void processTransaction(Transaction transaction) {
        if (transaction.getTransactionType() == TransactionType.REVOLUT_SIMPLE) {
            Account account = accountRepository.getAccount(transaction.getAccountId());

            validationService.validateTransaction(account, transaction);

            if (transaction.getTransactionAction() == TransactionAction.INCREASE) {
                accountRepository.increaseAccountBalance(account.getAccountId(), transaction.getAmount());
            } else if (transaction.getTransactionAction() == TransactionAction.DECREASE) {
                accountRepository.decreaseAccountBalance(account.getAccountId(), transaction.getAmount());
            } else {
                throw new TransactionRuntimeException(String.format("Non supported transaction action. transaction %s", transaction));
            }

        } else {
            throw new TransactionRuntimeException(String.format("Only Revolut simple transactions are supported yet. transaction %s", transaction));
        }

    }


}
