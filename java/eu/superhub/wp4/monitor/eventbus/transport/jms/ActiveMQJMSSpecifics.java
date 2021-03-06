package eu.superhub.wp4.monitor.eventbus.transport.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQJMSSpecifics implements IJMSSpecifics {
	private Session session;

	public Session getSession(String host, String port) throws NamingException,
			JMSException {
		ActiveMQConnectionFactory factory;
		Connection connection;
		Session session;

		factory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);

		connection = factory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		connection.start();

		this.session = session;

		return session;
	}

	public Topic getTopic(String code) throws NamingException, JMSException {
		return session.createTopic(code.replace('/', '_'));
	}
}
