package com.revolut.revolutaccountmanager.service;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

@Service
public class MessageConsumer {
    private final static String URL = "tcp://localhost:61616";
    private final static String QUEUE_NAME = "revolut.transaction.created.event";

    private javax.jms.MessageConsumer consumer;

    @Inject
    public MessageConsumer() throws JMSException {
        Connection connection = ((ConnectionFactory) new ActiveMQConnectionFactory(URL)).createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        consumer = session.createConsumer(session.createQueue(QUEUE_NAME));
    }

    public javax.jms.MessageConsumer getConsumer() {
        return consumer;
    }
}
