package com.systex.jbranch.platform.common.communication.jms;


import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;
import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import java.lang.Thread;
import java.lang.Runnable;

public class MultiUseConnection implements Connection,ExceptionListener {

	private Connection target;	
	private AtomicLong numbersInUse = new AtomicLong(0);//Connection同時被使用的數量
	private AtomicLong idleBeginning = new AtomicLong(0);//idle的開始時間
	private final Object finalizerGuardian = new Object(){
		protected void finalize() throws Throwable
		{
			destory();
		}
	};
	private ExceptionListener defaultExceptionListener = null; 
	private Vector<ExceptionListener> exceptionListeners = new Vector<ExceptionListener>();
	
	
	
	public static MultiUseConnection valueOf(Connection connection)
	{
		if(connection==null)
			return null;
		else
			return new MultiUseConnection(connection);
	}
	
	private MultiUseConnection(Connection target)
	{		
		this.target = target;
	}
	
	public void setIdleBeginning(long idleBeginning)
	{
		this.idleBeginning.set(idleBeginning);
	} 
	
	public long getIdleBeginning()
	{
		return idleBeginning.get();
	}
	
	public long getNumbersInUse()
	{
		return numbersInUse.get();
	}
		
	public void destory()
	{
		try
		{
			Thread thread = new Thread(
					new Runnable()
					{
						private Connection conn = target;
						public void run()
						{
							try
							{
								conn.close();
							}
							catch(Exception err)
							{}
						}
					}
			);
			thread.start();
		}
		catch(Exception err){}
	}

	public void close() throws JMSException 
	{
		if(numbersInUse.decrementAndGet() == 0)idleBeginning.set(System.currentTimeMillis());
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

	public void setDefaultExceptionListener(ExceptionListener listener) throws JMSException
	{
		defaultExceptionListener = listener;
		target.setExceptionListener(this);
	}
	
	public void setExceptionListener(ExceptionListener listener)
			throws JMSException {
		
		if(listener!=null)
		{
			if(!exceptionListeners.contains(listener))exceptionListeners.addElement(listener);			
		}
	}
	
	public void onException(final JMSException exception)
	{
		try
		{
			if(defaultExceptionListener!=null)
				defaultExceptionListener.onException(exception);
		}
		catch(Exception err)
		{}
				
		for(int i = exceptionListeners.size()-1;i>=0;i--)
		{	
			try
			{				
				final ExceptionListener listener = exceptionListeners.get(i);
				exceptionListeners.removeElementAt(i);
				Thread trigger = new Thread(
						new Runnable()
						{
							private ExceptionListener el = listener;
							public void run()
							{
								try
								{
									el.onException(exception);
								}
								catch(Exception ex){}								
							}
						}
				);
				trigger.start();
			}
			catch(Exception err)
			{}
		}
	}
	
	public void start() throws JMSException {

		idleBeginning.set(0);
		numbersInUse.incrementAndGet();
		target.start();
	}

	public void stop() throws JMSException {}

}
