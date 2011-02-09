package net.sf.ictalive.eventbus.transport;

import java.io.IOException;

public interface IEventBusTransportListener
{

	void dispatch(String xml) throws IOException;

}
