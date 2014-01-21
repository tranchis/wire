package eu.superhub.wp4.monitor.eventbus;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.BlockingQueue;

import net.sf.ictalive.runtime.event.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp4.monitor.eventbus.transport.IEventBusTransport;
import eu.superhub.wp4.monitor.metamodel.utils.Serialiser;

public class ThOutput extends Thread {
    private IEventBusTransport		transport;
    private BlockingQueue<Event>	output;
    private Serialiser<Event>		s;
    private Logger			logger;

    public ThOutput(IEventBusTransport transport, BlockingQueue<Event> output,
	    Serialiser<Event> s) {
	logger = LoggerFactory.getLogger(getClass());
	this.transport = transport;
	this.output = output;
	this.s = s;
    }

    public void run() {
	String xml;
	Event obj;

	while (true) {
	    try {
		obj = output.take();
		obj.setTimestamp(Calendar.getInstance().getTime());
		xml = s.serialise(obj);

		transport.publish(xml);
		logger.debug("Remaining on queue: " + output.size());
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }
}
