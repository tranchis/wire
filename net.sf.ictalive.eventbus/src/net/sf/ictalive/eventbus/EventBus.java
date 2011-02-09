package net.sf.ictalive.eventbus;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.sf.ictalive.eventbus.exception.EventBusConnectionException;
import net.sf.ictalive.eventbus.transport.IEventBusTransport;
import net.sf.ictalive.eventbus.transport.IEventBusTransportListener;
import net.sf.ictalive.eventbus.transport.JMSEventBusTransport;
import net.sf.ictalive.metamodel.utils.Serialiser;
import net.sf.ictalive.runtime.NormInstances.NormInstancesPackage;
import net.sf.ictalive.runtime.event.Actor;
import net.sf.ictalive.runtime.event.Event;
import net.sf.ictalive.runtime.event.EventFactory;
import net.sf.ictalive.runtime.event.EventPackage;
import net.sf.ictalive.runtime.event.Key;
import net.sf.ictalive.runtime.fact.Content;
import net.sf.ictalive.runtime.fact.Fact;
import net.sf.ictalive.runtime.fact.FactFactory;

public class EventBus implements IEventBusTransportListener
{
	private String					host, name, url;
	private EventBusListener		ebl;
	private IEventBusTransport		transport;
	private BlockingQueue<Event>	queue, output;
	private Serialiser<Event>		s;
	private ThOutput				th;
	private long					lastReceived;
	private boolean					debug = false;
	
	private final static String				defaultHost			= "localhost";
	private final static IEventBusTransport	defaultTransport	= new JMSEventBusTransport();
	
	static
	{
		// TODO: Hotfix! Warn Thanos
		NormInstancesPackage.eINSTANCE.eClass();
	}
	
	public EventBus() throws EventBusConnectionException
	{
		host = defaultHost;
		ebl = null;
		this.transport = defaultTransport;
		initialise("/topic/ExampleTopic");
	}
	
	public EventBus(IEventBusTransport transport) throws EventBusConnectionException
	{
		host = defaultHost;
		ebl = null;
		this.transport = transport;
		initialise("/topic/ExampleTopic");
	}
	
	public EventBus(String host) throws EventBusConnectionException
	{
		this.host = host;
		ebl = null;
		this.transport = defaultTransport;
		initialise("/topic/ExampleTopic");
	}
	
	public EventBus(String host, IEventBusTransport transport) throws EventBusConnectionException
	{
		this.host = host;
		ebl = null;
		this.transport = transport;
		initialise("/topic/ExampleTopic");
	}
	
	public EventBus(EventBusListener ebl) throws EventBusConnectionException
	{
		host = defaultHost;
		this.ebl = ebl;
		this.transport = defaultTransport;
		initialise("/topic/ExampleTopic");
	}
	
	public EventBus(EventBusListener ebl, IEventBusTransport transport) throws EventBusConnectionException
	{
		host = defaultHost;
		this.ebl = ebl;
		this.transport = transport;
		initialise("/topic/ExampleTopic");
	}
	
	public EventBus(String host, EventBusListener ebl) throws EventBusConnectionException
	{
		this.host = host;
		this.ebl = ebl;
		this.transport = defaultTransport;
		initialise("/topic/ExampleTopic");
	}
	
	public EventBus(String host, EventBusListener ebl, IEventBusTransport transport) throws EventBusConnectionException
	{
		this.host = host;
		this.ebl = ebl;
		this.transport = transport;
		initialise("/topic/ExampleTopic");
	}
	
	public Event take() throws InterruptedException
	{
		return queue.take();
	}
	
	public Event takeNow()
	{
		return queue.poll();
	}

	public void initialise(String code) throws EventBusConnectionException
	{
		if(queue == null)
		{
			queue = new LinkedBlockingQueue<Event>();
		}
		if(output == null)
		{
			output = new LinkedBlockingQueue<Event>();
		}
		
		if(s == null)
		{
			s = new Serialiser<Event>(EventPackage.class);
		}
		
		transport.initialise(code, host, this);
		
		if(th == null)
		{
			th = new ThOutput(transport, output, s);
			th.start();
		}
		
		lastReceived = System.currentTimeMillis();
	}
	
	public void subscribe(String code) throws EventBusConnectionException
	{
		initialise(code);
	}
	
	public void publish(Event obj) throws IOException
	{
		output.add(obj);
	}

	@Override
	public void dispatch(String xml) throws IOException
	{
		Event	ev;
		
		ev = s.deserialiseAndFree(xml);
		if(transport.isValid(ev.getTimestamp()))
		{
			if(ebl != null)
			{
				ebl.onEvent(ev);
			}
			queue.add(ev);
		}
		
		if(debug)
		{
			System.out.println("Time since last received: " + (System.currentTimeMillis() - lastReceived) + "ms");
		}
		lastReceived = System.currentTimeMillis();
	}

	public synchronized void removeListener(EventBusListener ebl)
	{
		if(this.ebl == ebl)
		{
			ebl = null;
		}
	}
	
	public void setActor(String name, String url)
	{
		this.name = name;
		this.url = url;
	}
	
	public synchronized void publish(Fact f) throws IOException
	{
		Event	ev;
		Content	c;
		Key		k;
		Actor	actor;
		
		if(name == null || url == null)
		{
			throw new UnsupportedOperationException("Must setActor() before publish(Fact)!");
		}
		else
		{
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

	public int available()
	{
		return queue.size();
	}
}
