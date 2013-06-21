package eu.superhub.wp4.monitor.eventbus;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.sf.ictalive.runtime.NormInstances.NormInstancesPackage;
import net.sf.ictalive.runtime.event.Actor;
import net.sf.ictalive.runtime.event.Event;
import net.sf.ictalive.runtime.event.EventFactory;
import net.sf.ictalive.runtime.event.EventPackage;
import net.sf.ictalive.runtime.event.Key;
import net.sf.ictalive.runtime.fact.Content;
import net.sf.ictalive.runtime.fact.Fact;
import net.sf.ictalive.runtime.fact.FactFactory;
import eu.superhub.wp4.monitor.eventbus.exception.EventBusConnectionException;
import eu.superhub.wp4.monitor.eventbus.transport.IEventBusTransport;
import eu.superhub.wp4.monitor.eventbus.transport.IEventBusTransportListener;
import eu.superhub.wp4.monitor.eventbus.transport.JMSEventBusTransport;
import eu.superhub.wp4.monitor.metamodel.utils.Serialiser;

public class EventBus implements IEventBusTransportListener {
	private String					host, port, name, url;
	private EventBusListener		ebl;
	private IEventBusTransport		transport;
	private BlockingQueue<Event>	queue;
	private BlockingQueue<String>	output; // TODO: Move completely to ThOutput
	private Serialiser<Event>		s;
	private ThOutput				th;
	private long					lastReceived;
	private boolean					debug = false;
	private boolean					active = true;

	private final static String defaultHost = "localhost";
	private final static String defaultPort = "7676";
	private final static IEventBusTransport defaultTransport = new JMSEventBusTransport(
			true);

	static {
		// TODO: Hotfix! Warn Thanos
		NormInstancesPackage.eINSTANCE.eClass();
	}

	public EventBus() throws EventBusConnectionException {
		host = defaultHost;
		port = defaultPort;
		ebl = null;
		this.transport = defaultTransport;
		initialise("/topic/ExampleTopic");
	}

	public EventBus(IEventBusTransport transport)
			throws EventBusConnectionException {
		host = defaultHost;
		port = defaultPort;
		ebl = null;
		this.transport = transport;
		initialise("/topic/ExampleTopic");
	}

	public EventBus(String host, String port)
			throws EventBusConnectionException {
		this.host = host;
		this.port = port;
		ebl = null;
		this.transport = defaultTransport;
		initialise("/topic/ExampleTopic");
	}

	public EventBus(String host, String port, boolean bActiveMQ)
			throws EventBusConnectionException {
		this.host = host;
		this.port = port;
		ebl = null;
		this.transport = new JMSEventBusTransport(bActiveMQ);
		initialise("/topic/ExampleTopic");
	}

	public EventBus(String host, String port, IEventBusTransport transport)
			throws EventBusConnectionException {
		this.host = host;
		this.port = port;
		ebl = null;
		this.transport = transport;
		initialise("/topic/ExampleTopic");
	}

	public EventBus(EventBusListener ebl) throws EventBusConnectionException {
		host = defaultHost;
		port = defaultPort;
		this.ebl = ebl;
		this.transport = defaultTransport;
		initialise("/topic/ExampleTopic");
	}

	public EventBus(EventBusListener ebl, IEventBusTransport transport)
			throws EventBusConnectionException {
		host = defaultHost;
		port = defaultPort;
		this.ebl = ebl;
		this.transport = transport;
		initialise("/topic/ExampleTopic");
	}

	public EventBus(String host, String port, EventBusListener ebl)
			throws EventBusConnectionException {
		this.host = host;
		this.port = port;
		this.ebl = ebl;
		this.transport = defaultTransport;
		initialise("/topic/ExampleTopic");
	}

	public EventBus(String host, String port, EventBusListener ebl,
			IEventBusTransport transport) throws EventBusConnectionException {
		this.host = host;
		this.port = port;
		this.ebl = ebl;
		this.transport = transport;
		initialise("/topic/ExampleTopic");
	}

	public Event take() throws InterruptedException {
		return queue.take();
	}

	public Event takeNow() {
		return queue.poll();
	}

	public void initialise(String code) throws EventBusConnectionException {
		if (queue == null) {
			queue = new LinkedBlockingQueue<Event>();
		}
		if (output == null) {
			output = new LinkedBlockingQueue<String>();
		}

		if (s == null) {
			s = new Serialiser<Event>(EventPackage.class);
		}

		transport.initialise(code, host, port, this);

		if (th == null) {
			th = new ThOutput(transport, output);
			th.start();
		}

		lastReceived = System.currentTimeMillis();
	}

	public void subscribe(String code) throws EventBusConnectionException {
		initialise(code);
	}

	public void publish(Event obj) throws IOException {
		th.add(obj);
	}

	public void publish(String obj) throws IOException {
		th.add(obj);
	}
	
	public synchronized void activateSubscription(boolean active) {
		this.active = active;
	}

	public void dispatch(String xml) throws IOException {
		Event ev;

		if (active) {
			ev = s.deserialiseAndFree(xml);
			if (transport.isValid(ev.getTimestamp())) {
				if (ebl != null) {
					ebl.onEvent(ev);
				}
				queue.add(ev);
			}

			if (debug) {
				System.out.println("Time since last received: "
						+ (System.currentTimeMillis() - lastReceived) + "ms");
			}
			lastReceived = System.currentTimeMillis();
		}
	}

	public synchronized void removeListener(EventBusListener ebl) {
		if (this.ebl == ebl) {
			ebl = null;
		}
	}

	public void setActor(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public synchronized void publish(Fact f) throws IOException {
		Event	ev;
		Content	c;
		Key		k;
		Actor	actor;

		if (name == null || url == null) {
			throw new UnsupportedOperationException(
					"Must setActor() before publish(Fact)!");
		} else {
			actor = EventFactory.eINSTANCE.createActor();
			actor.setName(name);
			actor.setUrl(url);
			ev = EventFactory.eINSTANCE.createEvent();
			c = FactFactory.eINSTANCE.createContent();
			k = EventFactory.eINSTANCE.createKey();
			ev.setAsserter(actor);
			c.setFact(f);
			ev.setContent(c);
			k.setId("" + System.currentTimeMillis() + new Random().nextLong());
			ev.setLocalKey(k);
			ev.setPointOfView(EventFactory.eINSTANCE.createObserverView());
			ev.setTimestamp(Calendar.getInstance().getTime());
			publish(ev);
		}
	}

	public int available() {
		return queue.size();
	}
	
	public int waitingForDispatch() {
		return output.size();
	}
}
