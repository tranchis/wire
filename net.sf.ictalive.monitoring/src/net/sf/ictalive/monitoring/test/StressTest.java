package net.sf.ictalive.monitoring.test;

import java.io.IOException;

import net.sf.ictalive.eventbus.EventBus;
import net.sf.ictalive.eventbus.exception.EventBusConnectionException;
import net.sf.ictalive.operetta.OM.Atom;
import net.sf.ictalive.operetta.OM.Constant;
import net.sf.ictalive.operetta.OM.OMFactory;
import net.sf.ictalive.runtime.event.Event;
import net.sf.ictalive.runtime.event.EventFactory;
import net.sf.ictalive.runtime.fact.Content;
import net.sf.ictalive.runtime.fact.FactFactory;
import net.sf.ictalive.runtime.fact.Message;
import net.sf.ictalive.runtime.fact.SendAct;

public class StressTest extends Thread
{

	/**
	 * @param args
	 * @throws EventBusConnectionException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws EventBusConnectionException, InterruptedException
	{
		EventBus	eb;
		Event		ev;
		Content		c;
		SendAct		sa;
		Message		ms;
		Atom		a;
		Constant	ct;
		
		eb = new EventBus("147.83.200.118");
		
		while(true)
		{
			ev = EventFactory.eINSTANCE.createEvent();
			c = FactFactory.eINSTANCE.createContent();
			sa = FactFactory.eINSTANCE.createSendAct();
			ms = FactFactory.eINSTANCE.createMessage();
			a = OMFactory.eINSTANCE.createAtom();
			c.setFact(sa);
			ev.setContent(c);
			ms.getObject().add(a);
			a.setPredicate("Unit");
			ct = OMFactory.eINSTANCE.createConstant();
			ct.setName(System.currentTimeMillis() + "");
			a.getArguments().add(ct);
			ct = OMFactory.eINSTANCE.createConstant();
			ct.setName("Peasant");
			a.getArguments().add(ct);
			sa.setSendMessage(ms);
			
			try
			{
				eb.publish(ev);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			sleep(200);
		}
	}
}
