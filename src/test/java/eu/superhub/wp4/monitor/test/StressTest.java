package eu.superhub.wp4.monitor.test;

import junit.framework.TestCase;
import eu.superhub.wp4.monitor.eventbus.exception.EventBusConnectionException;

public class StressTest extends TestCase {

    /**
     * @param args
     * @throws EventBusConnectionException
     * @throws InterruptedException
     */
	public static void testIntegration(){
	
	}
	/*
    public static void testIntegration() throws EventBusConnectionException,
	InterruptedException {
	EventBus eb;
	Event ev;
	Content c;
	SendAct sa;
	Message ms;
	Atom a;
	Constant ct;
        int max_loops = 0;           
        
        
        
        eb = new EventBus("tranchis.mooo.com", "61616");


            for(int i=0;i<max_loops;i++) {
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
    */
    
}
