package com.systex.esoaf.jms.pool;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueSender;

public class PooledQueueSender extends PooledProducer implements QueueSender {

    public PooledQueueSender(QueueSender messageProducer, Destination destination) throws JMSException {
        super(messageProducer, destination);
    }

    public void send(Queue queue, Message message, int i, int i1, long l) throws JMSException {
        getQueueSender().send(queue, message, i, i1, l);
    }

    public void send(Queue queue, Message message) throws JMSException {
        getQueueSender().send(queue, message);
    }

    public Queue getQueue() throws JMSException {
        return getQueueSender().getQueue();
    }


    protected QueueSender getQueueSender() {
        return (QueueSender) getMessageProducer();
    }

}
