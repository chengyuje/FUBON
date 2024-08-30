package com.systex.esoaf.jms.pool;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.TopicPublisher;

public class PooledTopicPublisher extends PooledProducer implements TopicPublisher {

    public PooledTopicPublisher(TopicPublisher messageProducer, Destination destination) throws JMSException {
        super(messageProducer, destination);
    }

    public Topic getTopic() throws JMSException {
        return getTopicPublisher().getTopic();
    }

    public void publish(Message message) throws JMSException {
        getTopicPublisher().publish((Topic) getDestination(), message);
    }

    public void publish(Message message, int i, int i1, long l) throws JMSException {
        getTopicPublisher().publish((Topic) getDestination(), message, i, i1, l);
    }

    public void publish(Topic topic, Message message) throws JMSException {
        getTopicPublisher().publish(topic, message);
    }

    public void publish(Topic topic, Message message, int i, int i1, long l) throws JMSException {
        getTopicPublisher().publish(topic, message, i, i1, l);
    }

    protected TopicPublisher getTopicPublisher() {
        return (TopicPublisher) getMessageProducer();
    }
}
