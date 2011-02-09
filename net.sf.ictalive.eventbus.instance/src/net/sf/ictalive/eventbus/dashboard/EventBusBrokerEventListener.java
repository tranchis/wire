package net.sf.ictalive.eventbus.dashboard;

import com.sun.messaging.jmq.jmsservice.BrokerEvent;
import com.sun.messaging.jmq.jmsservice.BrokerEventListener;

public class EventBusBrokerEventListener implements BrokerEventListener
{

	@Override
	public void brokerEvent(BrokerEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public boolean exitRequested(BrokerEvent arg0, Throwable arg1)
	{
		return true;
	}
}
