package com.revolut.revolutaccountmanager.repository.service;

import com.revolut.revolutaccountmanager.model.account.Account;
import com.revolut.revolutaccountmanager.model.exception.TransactionRuntimeException;
import com.revolut.revolutaccountmanager.model.transaction.Transaction;
import com.revolut.revolutaccountmanager.model.transaction.TransactionType;
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

    private TextMessage textMessage;
    private Account account;

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

        textMessage = new ActiveMQTextMessage();
        textMessage.setText("{\"transactionId\" : " + TRANSACTION_ID + "}");
        account = TestHelper.getAccount(ACCOUNT_ID);
        when(accountRepository.getAccount(ACCOUNT_ID)).thenReturn(account);
    }

    @Test
    public void onMessage_givenValidMessage_canIncreaseBalanceSuccessfully() throws JMSException {
        Transaction transaction = TestHelper.getTransaction(TRANSACTION_ID, ACCOUNT_ID, BigDecimal.TEN, Currency.getInstance("GBP"), TransactionType.REVOLUT_SIMPLE_INCREASE);
        when(transactionManagerClient.getTransactionById(TRANSACTION_ID)).thenReturn(transaction);

        accountService.onMessage(textMessage);

        verify(accountRepository).getAccount(ACCOUNT_ID);
        verify(accountRepository).increaseAccountBalance(ACCOUNT_ID, BigDecimal.TEN);
        verifyNoMoreInteractions(accountRepository);
        verify(transactionManagerClient, never()).createSimpleIncreaseTransactionRequest(any());
        verify(jmsCompleteMessageProducer).send(textMessage);
        verifyZeroInteractions(jmsFailedMessageProducer);
    }

    @Test
    public void onMessage_givenValidMessage_canDecreaseBalanceSuccessfully() throws JMSException {
        Transaction transaction = TestHelper.getTransaction(TRANSACTION_ID, ACCOUNT_ID, BigDecimal.TEN, Currency.getInstance("GBP"), TransactionType.REVOLUT_SIMPLE_DECREASE);
        when(transactionManagerClient.getTransactionById(TRANSACTION_ID)).thenReturn(transaction);

        accountService.onMessage(textMessage);

        verify(accountRepository).getAccount(ACCOUNT_ID);
        verify(accountRepository).decreaseAccountBalance(ACCOUNT_ID, BigDecimal.TEN);
        verifyNoMoreInteractions(accountRepository);
        verify(transactionManagerClient, never()).createSimpleIncreaseTransactionRequest(any());
        verify(jmsCompleteMessageProducer).send(textMessage);
        verifyZeroInteractions(jmsFailedMessageProducer);
    }

    @Test
    public void onMessage_givenValidMessage_canTransferBalanceSuccessfully() throws JMSException {
        Transaction transaction = TestHelper.getTransaction(TRANSACTION_ID, ACCOUNT_ID, BigDecimal.TEN, Currency.getInstance("GBP"), TransactionType.REVOLUT_TRANSFER);
        when(transactionManagerClient.getTransactionById(TRANSACTION_ID)).thenReturn(transaction);

        accountService.onMessage(textMessage);

        verify(accountRepository).getAccount(ACCOUNT_ID);
        verify(accountRepository).decreaseAccountBalance(ACCOUNT_ID, BigDecimal.TEN);
        verifyNoMoreInteractions(accountRepository);
        verify(transactionManagerClient).createSimpleIncreaseTransactionRequest(transaction);
        verify(jmsCompleteMessageProducer).send(textMessage);
        verifyZeroInteractions(jmsFailedMessageProducer);
    }

    @Test
    public void onMessage_givenInvalidMessage_sendsFailMessage() throws JMSException {
        Transaction transaction = TestHelper.getTransaction(TRANSACTION_ID, ACCOUNT_ID, BigDecimal.TEN, Currency.getInstance("GBP"));
        when(transactionManagerClient.getTransactionById(TRANSACTION_ID)).thenReturn(transaction);
        doThrow(new TransactionRuntimeException("Fail")).when(validationService).validateTransaction(account, transaction);

        accountService.onMessage(textMessage);

        verify(accountRepository).getAccount(ACCOUNT_ID);
        verifyNoMoreInteractions(accountRepository);
        verify(transactionManagerClient, never()).createSimpleIncreaseTransactionRequest(any());
        verify(jmsFailedMessageProducer).send(textMessage);
        verifyZeroInteractions(jmsCompleteMessageProducer);
    }

}
