package net.sf.ictalive.eventbus.transport;

import java.io.IOException;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.NamingException;

import net.sf.ictalive.eventbus.exception.EventBusConnectionException;
import net.sf.ictalive.eventbus.transport.jms.IJMSSpecifics;
import net.sf.ictalive.eventbus.transport.jms.MQJMSSpecifics;

public class JMSEventBusTransport implements IEventBusTransport, MessageListener
{
	private Session						session;
	private MessageConsumer				consumer;
	private MessageProducer				producer;
	private IEventBusTransportListener	transportListener;
	private IJMSSpecifics				jms;
	
	public JMSEventBusTransport()
	{
		this.jms = new MQJMSSpecifics();
	}

	@Override
	public void initialise(String code, String host, IEventBusTransportListener ebtl) throws EventBusConnectionException
	{
		Topic				topic;
		
		this.transportListener = ebtl;
		
		try
		{
			session = jms.getSession(host);
			topic = jms.getTopic(code);
			
			consumer = session.createConsumer(topic);
			consumer.setMessageListener(this);
			producer = session.createProducer(topic);
		}
		catch (NamingException ne)
		{
			throw new EventBusConnectionException(ne);
		}
		catch (JMSException e)
		{
			throw new EventBusConnectionException(e);
		}
	}
	
	@Override
	public boolean isValid(Date timestamp)
	{
		return true;
	}

	@Override
	public void publish(String xml)
	{
		Message	message;
		
		try
		{
			message = session.createTextMessage(xml);
			producer.send(message);
		}
		catch (JMSException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onMessage(Message arg0)
	{
		TextMessage			tm;
		String				st;
		
		if(arg0 instanceof TextMessage)
		{
			tm = (TextMessage)arg0;
			try
			{
				st = tm.getText();
				if(transportListener != null)
				{
					transportListener.dispatch(st);
				}
			}
			catch(JMSException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
