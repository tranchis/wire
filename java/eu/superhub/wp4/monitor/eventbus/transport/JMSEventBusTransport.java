package eu.superhub.wp4.monitor.eventbus.transport;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp4.monitor.eventbus.exception.EventBusConnectionException;
import eu.superhub.wp4.monitor.eventbus.transport.jms.ActiveMQJMSSpecifics;
import eu.superhub.wp4.monitor.eventbus.transport.jms.IJMSSpecifics;
import eu.superhub.wp4.monitor.eventbus.transport.jms.MQJMSSpecifics;

public class JMSEventBusTransport implements IEventBusTransport,
		MessageListener {
	private Session session;
	private MessageConsumer consumer;
	private MessageProducer producer;
	private IEventBusTransportListener transportListener;
	private IJMSSpecifics jms;
	private Logger logger;
	private String lastID;

	public JMSEventBusTransport(boolean bActiveMQ) {
		lastID = "InvalidID";
		logger = LoggerFactory.getLogger(getClass());
		if (bActiveMQ) {
			this.jms = new ActiveMQJMSSpecifics();
		} else {
			this.jms = new MQJMSSpecifics();
		}
	}

	public void initialise(String code, String host, String port,
			IEventBusTransportListener ebtl) throws EventBusConnectionException {
		Topic topic;

		this.transportListener = ebtl;

		try {
			session = jms.getSession(host, port);
			topic = jms.getTopic(code);

			consumer = session.createConsumer(topic);
			consumer.setMessageListener(this);
			producer = session.createProducer(topic);
		} catch (NamingException ne) {
			throw new EventBusConnectionException(ne);
		} catch (JMSException e) {
			throw new EventBusConnectionException(e);
		}
	}

	public boolean isValid(Date timestamp) {
		return true;
	}

	public void publish(String xml) {
		Message message;

		try {
			message = session.createTextMessage(xml);
			producer.send(message);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onMessage(Message arg0) {
		TextMessage tm;
		String st, newID;
		boolean isDifferent;

		try {
			// Incoming absurdity to deal with (potential?) bug in ActiveMQ Java
			// in which
			// messages come twice to the listener
			// TODO: Check if this is really the expected behaviour of ActiveMQ
			logger.debug(arg0.getJMSMessageID() + ":" + arg0.getJMSTimestamp()
					+ ":" + arg0.getJMSDestination());
			newID = arg0.getJMSMessageID();
			isDifferent = !(newID.equals(lastID));
			lastID = newID;
		} catch (JMSException e1) {
			logger.error("Error while retrieving message JMS properties: "
					+ e1.getMessage());
			// For reliability reasons, better to let pass this one, better to
			// receive the message anyway...
			isDifferent = true;
		}

		if (isDifferent && arg0 instanceof TextMessage) {
			tm = (TextMessage) arg0;
			try {
				st = tm.getText();
				if (transportListener != null) {
					logger.debug("Dispatching event: " + st);
					transportListener.dispatch(st);
				}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
