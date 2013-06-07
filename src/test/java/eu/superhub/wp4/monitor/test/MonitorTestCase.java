package eu.superhub.wp4.monitor.test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;
import net.sf.ictalive.operetta.OM.Atom;
import net.sf.ictalive.operetta.OM.Constant;
import net.sf.ictalive.operetta.OM.OMFactory;
import net.sf.ictalive.runtime.event.Actor;
import net.sf.ictalive.runtime.event.Event;
import net.sf.ictalive.runtime.event.EventFactory;
import net.sf.ictalive.runtime.fact.Content;
import net.sf.ictalive.runtime.fact.FactFactory;
import net.sf.ictalive.runtime.fact.Message;
import net.sf.ictalive.runtime.fact.SendAct;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp4.monitor.core.IMonitor;
import eu.superhub.wp4.monitor.eventbus.EventBus;
import eu.superhub.wp4.monitor.eventbus.exception.EventBusConnectionException;
import eu.superhub.wp4.monitor.iface.MonitorManager;
import eu.superhub.wp4.monitor.iface.impl.MonitorManagerImpl;

/**
 * Unit test for simple App.
 */
public class MonitorTestCase extends TestCase {
    private Logger	logger;
    private EventBus	eb;
    
    /**
     * Create the test case
     * 
     * @param testName
     *            name of the test case
     * @throws EventBusConnectionException 
     */
    public MonitorTestCase(String testName) throws EventBusConnectionException {
	super(testName);
	logger = LoggerFactory.getLogger(getClass());
	eb = new EventBus("tranchis.mooo.com", "61616");
    }

    /**
     * Test that initializes the monitor and sends some elements into the
     * EventBus
     */
    @Test
    public void testApp() {
	MonitorManager		mm;
	IMonitor		im;
	Collection<IMonitor>	cim;
	Iterator<IMonitor>	iim;

	try {
	    mm = new MonitorManagerImpl();
	    im = mm.createInstance("tranchis.mooo.com", 61616);
	    mm.start(im);

	    cim = mm.listInstances();
	    iim = cim.iterator();
	    while (iim.hasNext()) {
		logger.info("IMonitor: " + iim.next());
	    }
	    assert(cim.size() == 1);
	    
	    sendEvents(5);
	    Thread.sleep(3000);
	    mm.pause(im);
	    mm.restart(im);
	    sendEvents(5);
	    Thread.sleep(5000);
	    mm.stop(im);
	    logger.info("" + mm.getCount(im));
	    assert(mm.getCount(im) >= 10);
	} catch (IOException e) {
	    logger.error(e.getMessage());
	    fail(e.getMessage());
	} catch (EventBusConnectionException e) {
	    logger.error(e.getMessage());
	    fail(e.getMessage());
	} catch (InterruptedException e) {
	    logger.error(e.getMessage());
	    fail(e.getMessage());
	}
    }
    
    private void sendEvents(int total) throws EventBusConnectionException,
	InterruptedException {
	Event		ev;
	Content		c;
	SendAct		sa;
	Message		ms;
	Atom		a;
	Constant	ct;
	Actor		ac;

	for(int i=0;i<total;i++) {
	    ev = EventFactory.eINSTANCE.createEvent();
	    c = FactFactory.eINSTANCE.createContent();
	    sa = FactFactory.eINSTANCE.createSendAct();
	    ms = FactFactory.eINSTANCE.createMessage();
	    a = OMFactory.eINSTANCE.createAtom();
	    ac = EventFactory.eINSTANCE.createActor();
	    ac.setName("Agent" + i);
	    ev.setAsserter(ac);
	    c.setFact(sa);
	    ev.setContent(c);
	    ev.setTimestamp(Calendar.getInstance().getTime());
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
		logger.info("Sending " + i);
		eb.publish(ev);
	    } catch (IOException e) {
		fail(e.getMessage());
		e.printStackTrace();
	    }

	    Thread.sleep(100);
	}
    }
}
