package com.systex.jbranch.platform.common.communication.jms;

import javax.jms.Destination;
import javax.jms.ExceptionListener;

public interface JMSMessageProducerIF extends ExceptionListener {
	
	void commit() throws JmsException;
	
	void recover() throws JmsException;
	
	void rollback() throws JmsException;
	
	void close();
	
	Destination createDestination(String name) throws JmsException;
	
	void send(Destination destination, Object message, DeliveryMode deliveryMode, int priority, long timeToLive,MessagePostProcessorIF postProcessor) throws JmsException; 
	void send(Destination destination, Object message,MessagePostProcessorIF postProcessor) throws JmsException;	
		
	void send(Object message, DeliveryMode deliveryMode, int priority, long timeToLive,MessagePostProcessorIF postProcessor) throws JmsException;
	void send(Object message,MessagePostProcessorIF postProcessor) throws JmsException;
	
	void send(Destination destination, Object message, DeliveryMode deliveryMode, int priority, long timeToLive) throws JmsException; 
	void send(Destination destination, Object message) throws JmsException;	
		
	void send(Object message, DeliveryMode deliveryMode, int priority, long timeToLive) throws JmsException;
	void send(Object message) throws JmsException;
}
