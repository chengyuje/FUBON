package com.systex.jbranch.platform.common.communication.jms;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;



public class WrapperMultiUseConnection implements Connection {

	private Connection target = null;
	private volatile boolean isClosed = true;
	private final Object finalizerGuardian = new Object(){
		protected void finalize() throws Throwable
		{
			close();
		}
	};

	public static WrapperMultiUseConnection valueOf(Connection target)
	{
		if(target==null)
			return null;
		else
			return new WrapperMultiUseConnection(target);
	}
	
	private WrapperMultiUseConnection(Connection target)
	{
		this.target = target;
	}
	
	public void close() throws JMSException {

		if(!isClosed)
		{
			target.close();
			isClosed = true;
		}
	}

	public ConnectionConsumer createConnectionConsumer(Destination arg0,
			String arg1, ServerSessionPool arg2, int arg3) throws JMSException {

		return target.createConnectionConsumer(arg0, arg1, arg2, arg3);
	}

	public ConnectionConsumer createDurableConnectionConsumer(Topic arg0,
			String arg1, String arg2, ServerSessionPool arg3, int arg4)
			throws JMSException {

		return target.createDurableConnectionConsumer(arg0, arg1, arg2, arg3, arg4);
	}

	public Session createSession(boolean arg0, int arg1) throws JMSException {

		return target.createSession(arg0, arg1);
	}

	public String getClientID() throws JMSException {

		return target.getClientID();
	}

	public ExceptionListener getExceptionListener() throws JMSException {

		return target.getExceptionListener();
	}

	public ConnectionMetaData getMetaData() throws JMSException {

		return target.getMetaData();
	}

	public void setClientID(String arg0) throws JMSException {

		target.setClientID(arg0);

	}

	public void setExceptionListener(ExceptionListener arg0)
			throws JMSException {

		target.setExceptionListener(arg0);
	}

	public void start() throws JMSException {
		
		if(isClosed)
		{
			target.start();
			isClosed = false;
		}
	}

	public void stop() throws JMSException {
		target.stop();
	}

}
