package net.sf.ictalive.eventbus.transport;

import java.util.Date;

import net.sf.ictalive.eventbus.exception.EventBusConnectionException;

public interface IEventBusTransport
{
	void initialise(String code, String host, IEventBusTransportListener ebtl) throws EventBusConnectionException;

	void publish(String xml);

	boolean isValid(Date timestamp);
}
