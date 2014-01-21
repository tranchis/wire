package eu.superhub.wp4.monitor.eventbus.transport.jms;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.NamingException;

public interface IJMSSpecifics {

    Session getSession(String host, String port) throws NamingException,
	    JMSException;

    Topic getTopic(String code) throws NamingException, JMSException;

}
