package eu.superhub.wp4.monitor.eventbus.instance;

import com.sun.messaging.jmq.jmsservice.BrokerEvent;
import com.sun.messaging.jmq.jmsservice.BrokerEventListener;

public class EventBusBrokerEventListener implements BrokerEventListener {

    public void brokerEvent(BrokerEvent arg0) {
	// TODO Auto-generated method stub
    }

    public boolean exitRequested(BrokerEvent arg0, Throwable arg1) {
	return true;
    }
}
