package com.revolut.revolutaccountmanager.repository.service;

import com.revolut.revolutaccountmanager.model.account.Account;
import com.revolut.revolutaccountmanager.model.exception.TransactionRuntimeException;
import com.revolut.revolutaccountmanager.model.transaction.Transaction;
import com.revolut.revolutaccountmanager.model.transaction.TransactionAction;
import com.revolut.revolutaccountmanager.repository.AccountRepository;
import com.revolut.revolutaccountmanager.service.AccountService;
import com.revolut.revolutaccountmanager.service.MessageConsumer;
import com.revolut.revolutaccountmanager.service.MessageProducer;
import com.revolut.revolutaccountmanager.service.TransactionManagerClient;
import com.revolut.revolutaccountmanager.service.ValidationService;
import com.revolut.revolutaccountmanager.util.TestHelper;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.math.BigDecimal;
import java.util.Currency;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class AccountServiceTest {
    private static final int TRANSACTION_ID = 111;
    private static final int ACCOUNT_ID = 222;
    private AccountRepository accountRepository;
    private MessageConsumer messageConsumer;
    private TransactionManagerClient transactionManagerClient;
    private MessageProducer messageProducer;
    private ValidationService validationService;
    private javax.jms.MessageProducer jmsCompleteMessageProducer;
    private javax.jms.MessageProducer jmsFailedMessageProducer;

    private AccountService accountService;

    @Before
    public void setUp() throws JMSException {
        accountRepository = mock(AccountRepository.class);
        messageConsumer = mock(MessageConsumer.class);
        transactionManagerClient = mock(TransactionManagerClient.class);
        messageProducer = mock(MessageProducer.class);
        validationService = mock(ValidationService.class);

        when(messageConsumer.getConsumer()).thenReturn(mock(javax.jms.MessageConsumer.class));
        jmsCompleteMessageProducer = mock(javax.jms.MessageProducer.class);
        jmsFailedMessageProducer = mock(javax.jms.MessageProducer.class);
        when(messageProducer.getTransactionCompletedProducer()).thenReturn(jmsCompleteMessageProducer);
        when(messageProducer.getTransactionFailedProducer()).thenReturn(jmsFailedMessageProducer);

        accountService = new AccountService(accountRepository, messageConsumer, transactionManagerClient, messageProducer, validationService);
    }

    @Test
    public void onMessage_givenValidMessage_canProcessSuccessfully() throws JMSException {
        Transaction transaction = TestHelper.getTransaction(TRANSACTION_ID, ACCOUNT_ID, BigDecimal.TEN, Currency.getInstance("GBP"), TransactionAction.INCREASE);
        Account account = TestHelper.getAccount(ACCOUNT_ID);

        when(transactionManagerClient.getTransactionById(TRANSACTION_ID)).thenReturn(transaction);
        when(accountRepository.getAccount(ACCOUNT_ID)).thenReturn(account);

        TextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setText("{\"transactionId\" : " + TRANSACTION_ID + "}");

        accountService.onMessage(textMessage);

        verify(accountRepository).getAccount(ACCOUNT_ID);
        verify(accountRepository).increaseAccountBalance(ACCOUNT_ID, BigDecimal.TEN);
        verifyNoMoreInteractions(accountRepository);

        verify(jmsCompleteMessageProducer).send(textMessage);
        verifyZeroInteractions(jmsFailedMessageProducer);
    }

    @Test
    public void onMessage_givenValidMessage_canDecreaseBalanceSuccessfully() throws JMSException {
        Transaction transaction = TestHelper.getTransaction(TRANSACTION_ID, ACCOUNT_ID, BigDecimal.TEN, Currency.getInstance("GBP"), TransactionAction.DECREASE);
        Account account = TestHelper.getAccount(ACCOUNT_ID);
        when(transactionManagerClient.getTransactionById(TRANSACTION_ID)).thenReturn(transaction);
        when(accountRepository.getAccount(ACCOUNT_ID)).thenReturn(account);

        TextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setText("{\"transactionId\" : " + TRANSACTION_ID + "}");

        accountService.onMessage(textMessage);

        verify(accountRepository).getAccount(ACCOUNT_ID);
        verify(accountRepository).decreaseAccountBalance(ACCOUNT_ID, BigDecimal.TEN);
        verifyNoMoreInteractions(accountRepository);

        verify(jmsCompleteMessageProducer).send(textMessage);
        verifyZeroInteractions(jmsFailedMessageProducer);
    }

    @Test
    public void onMessage_givenInvalidMessage_sendsFailMessage() throws JMSException {
        Transaction transaction = TestHelper.getTransaction(TRANSACTION_ID, ACCOUNT_ID, BigDecimal.TEN, Currency.getInstance("GBP"), TransactionAction.DECREASE);
        Account account = TestHelper.getAccount(ACCOUNT_ID);
        when(transactionManagerClient.getTransactionById(TRANSACTION_ID)).thenReturn(transaction);
        when(accountRepository.getAccount(ACCOUNT_ID)).thenReturn(account);
        doThrow(new TransactionRuntimeException("Fail")).when(validationService).validateTransaction(account, transaction);

        TextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setText("{\"transactionId\" : " + TRANSACTION_ID + "}");

        accountService.onMessage(textMessage);

        verify(accountRepository).getAccount(ACCOUNT_ID);
        verifyNoMoreInteractions(accountRepository);

        verify(jmsFailedMessageProducer).send(textMessage);
        verifyZeroInteractions(jmsCompleteMessageProducer);
    }

}
