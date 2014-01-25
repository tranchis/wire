package eu.superhub.wp4.monitor.eventbus;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.BlockingQueue;

import net.sf.ictalive.runtime.event.Event;
import net.sf.ictalive.runtime.event.EventPackage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp4.monitor.eventbus.transport.IEventBusTransport;
import eu.superhub.wp4.monitor.metamodel.utils.Serialiser;

public class ThOutput extends Thread {
	private IEventBusTransport transport;
	private BlockingQueue<String> output;
	private Serialiser<Event> s;
	private Logger logger;

	public ThOutput(IEventBusTransport transport, BlockingQueue<String> output) {
		logger = LoggerFactory.getLogger(getClass());
		this.transport = transport;
		this.output = output;
		this.s = new Serialiser<Event>(EventPackage.class);
	}

	public void run() {
		String xml;

		while (true) {
			try {
				// long t = System.currentTimeMillis();
				xml = output.take();

				transport.publish(xml);
				// System.out.println("Remaining on queue: " + output.size() +
				// ", last process: " + (System.currentTimeMillis() - t));
				logger.debug("Remaining on queue: " + output.size());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void add(String xml) throws IOException {
		output.add(xml);
	}

	public void add(Event ev) throws IOException {
		ev.setTimestamp(Calendar.getInstance().getTime());
		output.add(s.serialise(ev));
	}
}
