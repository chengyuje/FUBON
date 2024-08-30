package com.systex.jbranch.platform.common.communication.debug;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;

import com.systex.jbranch.platform.common.communication.jms.MultiUseConnectionFactory;

import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.MessageProducer;
import javax.jms.MessageConsumer;
import javax.jms.Destination;
import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.TextMessage;


public class SpringDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
        Resource rs = new FileSystemResource("beans-config.xml");
        {
        BeanFactory factory = new XmlBeanFactory(rs);
        
        MultiUseConnectionFactory cf = null;
        try
        {
        	cf = (MultiUseConnectionFactory)factory.getBean("multiUseConnectionFactory");
        	Connection conn = cf.createConnection();
        	
        	Session pSession = conn.createSession(false,Session.AUTO_ACKNOWLEDGE);
        	Destination dest = pSession.createTopic("testQueue");
        	MessageProducer producer = pSession.createProducer(dest);
        	
        	Session cSession = conn.createSession(false,Session.AUTO_ACKNOWLEDGE);
        	MessageConsumer consumer = cSession.createConsumer(dest);
        	consumer.setMessageListener(new MessageListener()
        								{
        									public void onMessage(Message message)
        									{
        										try
        										{
        											System.out.println(((TextMessage)message).getText());
        										}
        										catch(Exception err)
        										{	
        										}
        									}
        								}
        	);
        	conn.start();
        	System.out.println("started...");
        	
        	String cmd = Console.ReadLine();
        	while(!cmd.equalsIgnoreCase("exit"))
        	{
        		TextMessage message = pSession.createTextMessage(cmd);
        		producer.send(message);
        		cmd = Console.ReadLine();
        	}
        	
        	producer.close();
        	consumer.close();
        }
        catch(Exception err)
        {
        	System.out.println(err.toString());
        }
        finally
        {
        	try
        	{
        		cf.close();
        	}
        	catch(Exception err)
        	{}
        }
        
        

        }
	}

}