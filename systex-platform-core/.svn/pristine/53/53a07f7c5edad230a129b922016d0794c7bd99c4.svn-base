package com.systex.jbranch.platform.common.communication.debug;
import java.io.*;
import java.util.Properties;
import java.util.Random;

import javax.jms.*;
import javax.naming.InitialContext;




public class testJms
{
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		System.out.println("server starting...");
		testServer svr = new testServer();
		System.out.println("server started");
		
		Console.WriteLine("producerCount,count,interval");
		
		String cmd;
		
		cmd = Console.ReadLine();
		while(!cmd.equalsIgnoreCase("exit"))
		{
			int producerCount;
			int count;
			int interval;
			try
			{
				producerCount = Integer.valueOf(cmd.split(",")[0]);
				count = Integer.valueOf(cmd.split(",")[1]);
				interval = Integer.valueOf(cmd.split(",")[2]);
			}
			catch(Exception err)
			{
				producerCount = 1;
				count = 60;
				interval = 1000;
			}
			svr.broadcast(producerCount ,count, interval);
			cmd = Console.ReadLine();
		}
	}
}


class testServer implements javax.jms.ExceptionListener{	

	InitialContext ic = null;
	ConnectionFactory cf = null;
	Connection conn = null;


	
	
	public void onException(JMSException err)
	{
		System.out.println("server onException:[" + err.toString() + "]");
	}
	
	public testServer()
	{
		try
		{						
			Properties env = new Properties();
			env.load(new FileInputStream("jndi.properties"));
			env.list(System.out);
			
			ic = new InitialContext(env);
			cf = (ConnectionFactory)ic.lookup("/ConnectionFactory");
			
			conn = cf.createConnection();
			conn.setExceptionListener(this);
		}
		catch(Exception err)
		{
			System.out.println("[error]");
			System.out.println(err.toString());
		}
		finally
		{
		}
	}
	
	public void broadcast(int producerCount,int count,int interval)
	{
		Random r = new Random();
		for(int i = 0;i<producerCount;i++)
		{
			try
			{
//				Thread.sleep(r.nextInt(500));
				Thread thread = new Thread(new producer(conn,i,count,interval));
				thread.start();
			}
			catch(Exception err)
			{
			}
		}
	}
}

class producer implements Runnable
{
	private Connection conn = null;
	private Session sess = null;
	private int id;
	private int count;
	private int interval;
	private MessageProducer producer = null;
	private Topic topic = null;
	
	public producer(Connection conn,int id,int count,int interval) throws JMSException
	{
		this.conn = conn;
		this.sess = this.conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		this.topic = this.sess.createTopic("testTopic");
		this.producer = this.sess.createProducer(this.topic);
		
		this.id = id;
		this.count = count;
		this.interval = interval;
	}
		
	public void run()
	{
		StringBuilder sb;
		for(int i=1;i<=this.count;i++)
		{
			sb = new StringBuilder();
			sb.append(" id : ");
			sb.append(this.id);
			sb.append(" broadcast message : ");
			sb.append(i);
			try
			{
				String value = sb.toString();
				
				Console.WriteLine(value);
				producer.send(sess.createTextMessage(value));
				Thread.sleep(this.interval);
			}
			catch(Exception err)
			{
				Console.WriteLine("broadcast error : " + err.toString());
			}
		}
	}
}