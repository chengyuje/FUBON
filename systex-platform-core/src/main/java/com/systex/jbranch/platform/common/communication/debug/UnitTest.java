package com.systex.jbranch.platform.common.communication.debug;



import com.systex.jbranch.platform.common.dataManager.UUID;


import javax.jms.Message;
import com.systex.jbranch.platform.common.communication.jms.JMSMessageProducerIF;
import com.systex.jbranch.platform.common.communication.jms.JMSMessageProducer;
import com.systex.jbranch.platform.common.communication.jms.MessagePostProcessorIF;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Iterator;
import javax.jms.JMSException;

public class UnitTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		LinkedBlockingQueue<JMSMessageProducerIF> queue = new LinkedBlockingQueue<JMSMessageProducerIF>(); 
		UUID uuid = new UUID();
		
		Console.WriteLine("input:");
		String cmd = Console.ReadLine();
		
		try
		{
			while(!cmd.equalsIgnoreCase("exit"))
			{
				if(cmd.equalsIgnoreCase("1"))
				{
					queue.put(JMSMessageProducer.getInstance(uuid));
				}
				else if(cmd.equalsIgnoreCase("0"))
				{
					try
					{
						queue.poll().close();
					}
					catch(Exception ex)
					{}
				}
				else
				{
					Iterator<JMSMessageProducerIF> iterator = queue.iterator();
					
					for(;iterator.hasNext();)
					{
						JMSMessageProducerIF p = iterator.next();
						p.send(cmd+ "__" + p.hashCode(),new MessagePostProcessorIF()
									{
										public Message postProcessMessage(Message message) throws JMSException
										{
											message.setStringProperty("userid", "allen");
											return message;
										}
									});
					}
				}
				cmd = Console.ReadLine();
			}
		}
		catch(Exception err)
		{
			
		}
		while(queue.size()>0)
		{
			queue.poll().close();
		}
	}
}
