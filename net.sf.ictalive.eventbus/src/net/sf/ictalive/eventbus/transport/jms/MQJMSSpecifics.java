package net.sf.ictalive.eventbus.transport.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.NamingException;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;

public class MQJMSSpecifics implements IJMSSpecifics
{
	private Session session;

	@Override
	public Session getSession(String host) throws NamingException,
			JMSException
	{
		ConnectionFactory	factory;
		Connection			connection;
		Session				session;
		
		factory = new ConnectionFactory();
		factory.setProperty(ConnectionConfiguration.imqBrokerHostName, host);
		factory.setProperty(ConnectionConfiguration.imqBrokerHostPort, "7676");
		
		connection = factory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		connection.start();
		
		this.session = session;
		
		return session;
	}

	@Override
	public Topic getTopic(String code) throws NamingException, JMSException
	{
		return session.createTopic(code.replace('/', '_'));
	}
}
