package com.systex.esoaf.jms.pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import javax.jms.Connection;
import javax.jms.QueueConnection;
import javax.jms.Session;
import javax.jms.TopicConnection;

public class PooledConnectionProxy implements InvocationHandler
{
	private ConnectionPool pool;
	private boolean stopped;
	
	public static Connection newProxyInstance(ConnectionPool connectionPool)
	{
		Connection connection=connectionPool.getConnection();
		List classes = new ArrayList(3);
		classes.add(Connection.class);
		if (connection instanceof TopicConnection) 
		{
			classes.add(TopicConnection.class);
		}
		if (connection instanceof QueueConnection)
		{
			classes.add(QueueConnection.class);
		}

		return (Connection) Proxy.newProxyInstance(
				connection.getClass().getClassLoader(),
				(Class[]) classes.toArray(new Class[classes.size()]),
				new PooledConnectionProxy(connectionPool));
	}
	
	private PooledConnectionProxy(ConnectionPool pool)
	{
		this.pool = pool;
        this.pool.incrementReferenceCount();
	}
	
	private Connection getConnection()
	{
		return pool.getConnection();
	}
	
	private Session createSession(Object[] args) throws Throwable
	{
		try
		{
			Method method=pool.getClass().getMethod("createSession",boolean.class,int.class);
			return (Session)method.invoke(pool, args);
		}
		catch (InvocationTargetException ex)
		{
			throw ex.getTargetException();
		}
	}
	
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		if ("equals".equals(method.getName())) 
		{
			return Boolean.valueOf(proxy == args[0]);
		}
		else if("toString".equals(method.getName()))
		{
			return "PooledConnection { " + pool + " }";
		}
		else if ("hashCode".equals(method.getName())) 
		{
			return new Integer(hashCode());
		}
		else if("stop".equals(method.getName()))//implements Connection
		{
			stopped = true;
			return null;
		}
		else if("start".equals(method.getName()))
		{
			pool.start();
			return null;
		}
		else if("close".equals(method.getName()))
		{
	        if (this.pool != null) 
	        {
	            this.pool.decrementReferenceCount();
	            this.pool = null;
	        }
	        return null;
		}
		else if("createSession".equals(method.getName())
				|| "createTopicSession".equals(method.getName())
				|| "createQueueSession".equals(method.getName()))
		{
			return createSession(args);
		}
		try
		{
			return method.invoke(getConnection(), args);
		}
		catch (InvocationTargetException ex) 
		{
			throw ex.getTargetException();
		}
	}
}