package eu.superhub.wp4.monitor.eventbus.transport;

import java.util.Date;

import eu.superhub.wp4.monitor.eventbus.exception.EventBusConnectionException;

public interface IEventBusTransport {
	void initialise(String code, String host, String port,
			IEventBusTransportListener ebtl) throws EventBusConnectionException;

	void publish(String xml);

	boolean isValid(Date timestamp);
}
