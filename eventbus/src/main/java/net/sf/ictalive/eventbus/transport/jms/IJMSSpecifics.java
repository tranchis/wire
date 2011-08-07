package net.sf.ictalive.eventbus.transport.jms;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.NamingException;

public interface IJMSSpecifics
{

	Session getSession(String host) throws NamingException, JMSException;

	Topic getTopic(String code) throws NamingException, JMSException;

}
