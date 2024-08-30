package com.systex.jbranch.platform.common.communication.jms;

import java.lang.Thread;
import java.lang.Runnable;
import java.util.Enumeration;
import java.util.Vector;
import java.io.Closeable;
import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import com.systex.jbranch.platform.common.communication.debug.DebugableIF;
import com.systex.jbranch.platform.common.communication.debug.DebuggerIF;
import com.systex.jbranch.platform.common.communication.debug.NullDebugger;
import com.systex.jbranch.platform.common.communication.jms.MultiUseConnection;
import com.systex.jbranch.platform.common.communication.jms.vo.JmsVoIF;



public class MultiUseConnectionFactory implements ConnectionFactory,Closeable,DebugableIF  {

	private DebuggerIF debugger = new NullDebugger();
	private ConnectionFactory connectionFactory = null;
	private Vector<MultiUseConnection> pool = new Vector<MultiUseConnection>();
	
	private JmsVoIF vo = null;
	
	//pool中最少的Connection數目
	private volatile long minActiveConnections = 1;
	//Connection可被同時使用的最大數目,0:不設限
	private volatile long maxNumbersPerConnection = 0;
	//Connection最長可idle期限,0:不設限
	private volatile long expiration = 0;
	
	private volatile boolean isClosed = false;
	
	private final Object finalizerGuardian = new Object(){
		protected void finalize() throws Throwable
		{
			close();
		}
	};
	
	public void setVO(JmsVoIF vo)
	{
		this.vo = vo;
	}
		
	public void clearExpiredObject()
	{
		MultiUseConnection conn = null;
		boolean expired;
		boolean idle;
		long idleBeginning;
		long now;
		long numbers;
		try
		{
				for(int i=pool.size();i>0;i--)
				{
					conn = pool.get(i-1);
					
					idleBeginning = conn.getIdleBeginning();
					now = System.currentTimeMillis();
					numbers = conn.getNumbersInUse();
					
					getDebugger().writeLine("[hashCode:%d] [idle:%d] [in use:%d]",this.hashCode(),(idleBeginning==0?0:now-idleBeginning),numbers);
					
					expired = expiration>0 && idleBeginning>0 && (now-idleBeginning > expiration);
					idle = (numbers == 0);
					
					if( expired && idle)
					{
						if(pool.size() > minActiveConnections)
						{
							getDebugger().writeLine("[hashCode:%d destory]",conn.hashCode());							
							pool.remove(conn);
							conn.destory();
						}
						else
						{
							getDebugger().writeLine("[hashCode:%d reset IdleBeginning]",conn.hashCode());
							conn.setIdleBeginning(0);
						}
					}
				}
		}
		catch(Exception err)
		{
			getDebugger().writeLine(err.toString());
		}
	}
			
	public MultiUseConnectionFactory()
	{
		Thread recyclingProcess = new Thread(
				new Runnable()
				{
					public void run()
					{
						while(!isClosed)
						{
							if(expiration>0)
							{
								try
								{
									
									clearExpiredObject();
								}catch(Exception err){}
								try
								{
									Thread.sleep(expiration);
								}catch(Exception err){}	
							}
						}
					}
				}
		);
		recyclingProcess.setDaemon(true); 
		recyclingProcess.start();
	}
	
	public void close()
	{
		isClosed = true;
		MultiUseConnection conn = null;
		for(int i=pool.size();i>0;i--)
		{
			try
			{
				conn = pool.get(i-1);
				pool.remove(conn);
				conn.destory();
			}
			catch(Exception err)
			{
				getDebugger().writeLine(err.toString());
			}				
		}
	}
	
	public void setMinActiveConnections(long size)
	{
		minActiveConnections = size;
	}
		
	public void setExpiration(long millisecond)
	{
		expiration = millisecond;
	}
	
	public void setMaxNumbersPerConnection(long numbers)
	{
		maxNumbersPerConnection = numbers;
	}
	
	public void setConnectionFactory(ConnectionFactory connectionFactory)
	{
		this.connectionFactory = connectionFactory;
	}
	
	public void setDebugger(DebuggerIF debugger)
	{
		if(debugger!=null)
			this.debugger = debugger;
		else
			this.debugger = new NullDebugger();
	}
	
	public DebuggerIF getDebugger()
	{
		return debugger; 
	}
	
	public Connection createConnection() throws JMSException {

		try
		{			
			if(pool.size()>=minActiveConnections)
			{
				long now = System.currentTimeMillis();
				boolean expired;
				long idleBeginning;
				boolean activable;
				long inUse = maxNumbersPerConnection;
				
				MultiUseConnection conn = null;
				Enumeration<MultiUseConnection> e = pool.elements();
				while(e.hasMoreElements())
				{
					MultiUseConnection element = e.nextElement();
					idleBeginning = element.getIdleBeginning();
					
					expired = (expiration>0) && (idleBeginning>0) && (now - idleBeginning>expiration);			
					activable = maxNumbersPerConnection <= 0 ? true :(element.getNumbersInUse()<maxNumbersPerConnection); 
					
					if( !expired && activable)
					{
						if(element.getNumbersInUse()<inUse)
						{
							inUse = element.getNumbersInUse();
							conn = element;
						}
					}
				}
				if(conn!=null)
					return WrapperMultiUseConnection.valueOf(conn);
			}
			
			{
				final MultiUseConnection conn = MultiUseConnection.valueOf(connectionFactory.createConnection(vo.getUserName(),vo.getPassword()));
				if(vo.getClientID()!=null)conn.setClientID(vo.getClientID());
				conn.setDefaultExceptionListener(
						new ExceptionListener()
						{
							private MultiUseConnection connection = conn;
							public void onException(JMSException exception)
							{
								try
								{
									pool.remove(connection);
									connection.destory();
									getDebugger().writeLine("defaultExceptionListener errmsg:[%s]",exception.getMessage());
								}
								catch(Exception err){}
							}
						}
					);
				pool.addElement(conn);
				return WrapperMultiUseConnection.valueOf(conn);
			}

		}
		catch(JMSException err)
		{
			getDebugger().writeLine(err.toString());
			
			throw err;
		}
		catch(Exception err)
		{
			getDebugger().writeLine(err.toString());
			
			JMSException jmsException = new JMSException(err.getMessage());
			jmsException.setLinkedException(err);
			throw jmsException;
		}
	}

	public Connection createConnection(String arg0, String arg1)
			throws JMSException {

		return createConnection();
	}

}
