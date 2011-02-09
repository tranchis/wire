package net.sf.ictalive.eventbus;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.BlockingQueue;

import net.sf.ictalive.eventbus.transport.IEventBusTransport;
import net.sf.ictalive.metamodel.utils.Serialiser;
import net.sf.ictalive.runtime.event.Event;

public class ThOutput extends Thread
{
	private IEventBusTransport		transport;
	private BlockingQueue<Event>	output;
	private Serialiser<Event>		s;

	public ThOutput(IEventBusTransport transport, BlockingQueue<Event> output, Serialiser<Event> s)
	{
		this.transport = transport;
		this.output = output;
		this.s = s;
	}

	public void run()
	{
		String	xml;
		Event	obj;

		while(true)
		{
			try
			{
				obj = output.take();
				obj.setTimestamp(Calendar.getInstance().getTime());
				xml = s.serialise(obj);
				
				transport.publish(xml);
//				System.out.println("Remaining on queue: " + output.size());
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
