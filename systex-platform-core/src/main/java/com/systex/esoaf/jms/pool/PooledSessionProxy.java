package com.systex.esoaf.jms.pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSession;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;
import javax.jms.XASession;

import com.systex.esoaf.jms.AlreadyClosedException;
import com.systex.esoaf.jms.pool.SessionPool;

public class PooledSessionProxy implements InvocationHandler
{
	private Session session;
    private SessionPool sessionPool;
    private MessageProducer messageProducer;
    private QueueSender queueSender;
    private TopicPublisher topicPublisher;
    private boolean transactional = true;
    private final CopyOnWriteArrayList<MessageConsumer> consumers = new CopyOnWriteArrayList<MessageConsumer>();
    private final CopyOnWriteArrayList<QueueBrowser> browsers = new CopyOnWriteArrayList<QueueBrowser>();
	
	public static ISessionProxy newProxyInstance(Session session,SessionPool sessionPool)
	{
		List classes = new ArrayList(5);
		classes.add(ISessionProxy.class);
		classes.add(Session.class);
		if (session instanceof QueueSession) 
		{
			classes.add(QueueSession.class);
		}
		if (session instanceof TopicSession)
		{
			classes.add(TopicSession.class);
		}
		if (session instanceof XASession)
		{
			classes.add(XASession.class);
		}

		PooledSessionProxy handler=new PooledSessionProxy(session,sessionPool);
		ISessionProxy proxy=(ISessionProxy) Proxy.newProxyInstance(
				session.getClass().getClassLoader(),
				(Class[]) classes.toArray(new Class[classes.size()]),
				handler);
		
		return proxy;
	}
	
	private PooledSessionProxy(Session session,SessionPool sessionPool)
	{
        this.session = session;
        this.sessionPool = sessionPool;
        try 
        {
			this.transactional = session.getTransacted();
		}
        catch (JMSException e) 
        {}
	}

	private Session getInternalSession() throws AlreadyClosedException
	{
        if (session == null)
        {
            throw new AlreadyClosedException("The session has already been closed.");
        }
        return session;
	}

	
    private MessageProducer getMessageProducer() throws JMSException {
        if (messageProducer == null) {
            messageProducer = (MessageProducer) getInternalSession().createProducer(null);
        }
        return messageProducer;
    }

    private QueueSender getQueueSender() throws JMSException {
        if (queueSender == null) {
            queueSender = ((QueueSession)getInternalSession()).createSender(null);
        }
        return queueSender;
    }

    private TopicPublisher getTopicPublisher() throws JMSException {
        if (topicPublisher == null) {
            topicPublisher = ((TopicSession)getInternalSession()).createPublisher(null);
        }
        return topicPublisher;
    }	
	
    private QueueBrowser addQueueBrowser(QueueBrowser browser) 
    {
        browsers.add(browser);
        return browser;
    }

    private MessageConsumer addConsumer(MessageConsumer consumer) 
    {
        consumers.add(consumer);
        return consumer;
    }

    private TopicSubscriber addTopicSubscriber(TopicSubscriber subscriber) 
    {
        consumers.add(subscriber);
        return subscriber;
    }

    private QueueReceiver addQueueReceiver(QueueReceiver receiver) 
    {
        consumers.add(receiver);
        return receiver;
    }
    
    private Object invoke(Method method, Object[] args) throws Throwable
    {
    	return method.invoke(session, args);
    }

    private void close(ISessionProxy proxy) throws JMSException
    {
    	try
    	{
    		session.setMessageListener(null);	
    	}
    	catch(Exception e)
    	{
    		//some jms products may not set MessageListener to null (like jboss message service)!
    	}

        // Close any consumers and browsers that may have been created.
        for (Iterator<MessageConsumer> iter = consumers.iterator(); iter.hasNext();) {
            MessageConsumer consumer = iter.next();
            consumer.close();
        }
        consumers.clear();

        for (Iterator<QueueBrowser> iter = browsers.iterator(); iter.hasNext();) {
            QueueBrowser browser = iter.next();
            browser.close();
        }
        browsers.clear();

        // maybe do a rollback?
        if (transactional) 
        {
            try
            {
                session.rollback();
            }
            catch (JMSException e) 
            {
                // lets close the session and not put the session back into
                // the pool
                try 
                {
                    session.close();
                } 
                catch (JMSException e1) 
                {
                }
                session = null;
                return;
            }
        }
        sessionPool.returnSession(proxy);
    }
    
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable 
	{
		if ("equals".equals(method.getName())) 
		{
			return Boolean.valueOf(proxy == args[0]);
		}
		else if ("hashCode".equals(method.getName())) 
		{
			return new Integer(hashCode());
		}
		else if ("close".equals(method.getName()))//implements Session
		{
			close((ISessionProxy)proxy);
			return null;
		}
		else if ("createProducer".equals(method.getName()))
		{
			return new PooledProducer(getMessageProducer(), (Destination)args[0]);
		}
		else if ("createConsumer".equals(method.getName()))
		{
			return addConsumer((MessageConsumer)invoke(method, args));
		}
		else if ("createSender".equals(method.getName()))//implements QueueSession
		{
			return new PooledQueueSender(getQueueSender(), (Queue)args[0]);
		}
		else if ("createReceiver".equals(method.getName()))
		{
			return addQueueReceiver((QueueReceiver)invoke(method, args));
		}
		else if ("createBrowser".equals(method.getName()))
		{
			return addQueueBrowser((QueueBrowser)invoke(method, args));
		}
		else if ("createPublisher".equals(method.getName()))//implements TopicSession
		{
			return new PooledTopicPublisher(getTopicPublisher(), (Topic)args[0]);
		}
		else if ("createSubscriber".equals(method.getName()))
		{
			return addTopicSubscriber((TopicSubscriber)invoke(method, args));
		}
		else if ("createDurableSubscriber".equals(method.getName()))
		{
			return addTopicSubscriber((TopicSubscriber)invoke(method, args));
		}
		else if ("getInternalSession".equals(method.getName()))//implements ISessionProxy
		{
			return this.getInternalSession();
		}
		try
		{
			return invoke(method, args);
		}
		catch (InvocationTargetException ex) 
		{
			throw ex.getTargetException();
		}
	}
}
