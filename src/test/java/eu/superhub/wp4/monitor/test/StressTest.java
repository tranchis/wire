package eu.superhub.wp4.monitor.test;

import java.io.IOException;

import junit.framework.TestCase;

import eu.superhub.wp4.monitor.eventbus.EventBus;
import eu.superhub.wp4.monitor.eventbus.exception.EventBusConnectionException;

import net.sf.ictalive.operetta.OM.Atom;
import net.sf.ictalive.operetta.OM.Constant;
import net.sf.ictalive.operetta.OM.OMFactory;
import net.sf.ictalive.runtime.event.Event;
import net.sf.ictalive.runtime.event.EventFactory;
import net.sf.ictalive.runtime.fact.Content;
import net.sf.ictalive.runtime.fact.FactFactory;
import net.sf.ictalive.runtime.fact.Message;
import net.sf.ictalive.runtime.fact.SendAct;

public class StressTest extends TestCase {

    /**
     * @param args
     * @throws EventBusConnectionException
     * @throws InterruptedException
     */
    public static void test() throws EventBusConnectionException,
	InterruptedException {
	EventBus eb;
	Event ev;
	Content c;
	SendAct sa;
	Message ms;
	Atom a;
	Constant ct;

	eb = new EventBus("tranchis.mooo.com", "61616");

	for(int i=0;i<100;i++) {
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

	    try {
		System.out.println("Sending " + i);
		eb.publish(ev);
	    } catch (IOException e) {
		fail(e.getMessage());
		e.printStackTrace();
	    }

	    Thread.sleep(200);
	}
    }
}
