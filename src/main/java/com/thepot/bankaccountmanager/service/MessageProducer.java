package com.thepot.bankaccountmanager.service;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.jvnet.hk2.annotations.Service;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

@Service
public class MessageProducer {
    private final static String URL = "tcp://localhost:61616";
    private final static String QUEUE_NAME_COMPLETED = "transaction.completed.event";
    private final static String QUEUE_NAME_FAILED = "transaction.failed.event";

    private final javax.jms.MessageProducer transactionCompletedProducer;
    private final javax.jms.MessageProducer transactionFailedProducer;

    public MessageProducer() throws JMSException {
        Connection connection = ((ConnectionFactory) new ActiveMQConnectionFactory(URL)).createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        transactionCompletedProducer = session.createProducer(session.createQueue(QUEUE_NAME_COMPLETED));
        transactionFailedProducer = session.createProducer(session.createQueue(QUEUE_NAME_FAILED));
    }

    public javax.jms.MessageProducer getTransactionCompletedProducer() {
        return transactionCompletedProducer;
    }

    public javax.jms.MessageProducer getTransactionFailedProducer() {
        return transactionFailedProducer;
    }
}
