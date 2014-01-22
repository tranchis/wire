package eu.superhub.wp4.monitor.eventbus.transport;

import java.io.IOException;

public interface IEventBusTransportListener {

    void dispatch(String xml) throws IOException;

}
