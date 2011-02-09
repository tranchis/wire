package net.sf.ictalive.eventbus.test;

import java.io.IOException;

import net.sf.ictalive.eventbus.EventBus;
import net.sf.ictalive.eventbus.exception.EventBusConnectionException;
import net.sf.ictalive.runtime.event.Actor;
import net.sf.ictalive.runtime.event.Event;
import net.sf.ictalive.runtime.event.EventFactory;
import net.sf.ictalive.runtime.fact.Content;
import net.sf.ictalive.runtime.fact.FactFactory;
import net.sf.ictalive.runtime.fact.Message;
import net.sf.ictalive.runtime.fact.SendAct;

public class EventBusTest
{
	public static void main(String args[]) throws InterruptedException, IOException, EventBusConnectionException
	{
		EventBus	eb;
		Event dummyEvent;
		SendAct dummyFact;
		Actor senderAgent;
		Actor reciverAgent;
		Content dummyContent;
		Message dummyMess; 
//		Serialiser<Event>	s;

		dummyEvent = EventFactory.eINSTANCE.createEvent();
		dummyFact = FactFactory.eINSTANCE.createSendAct(); 
		dummyContent= FactFactory.eINSTANCE.createContent();
		dummyMess = FactFactory.eINSTANCE.createMessage();
		dummyFact.setSendMessage(dummyMess); 
		senderAgent = EventFactory.eINSTANCE.createActor();
		senderAgent.setName("sfadsf");
		senderAgent.setUrl("localhost");
		reciverAgent = EventFactory.eINSTANCE.createActor();
		reciverAgent.setName("dfasdf");
		reciverAgent.setUrl("localhost");
		dummyFact.setSender(senderAgent);
		dummyFact.setReceiver(reciverAgent);
		dummyContent.setFact(dummyFact);
		dummyEvent.setContent(dummyContent);
		dummyEvent.setAsserter(senderAgent);
		
//		s = new Serialiser<Event>(EventPackage.class);
//		dummyEvent = s.deserialise(event1);
		
		//dummyEvent.setAsserter(senderAgent);
		eb = new EventBus();
		
//		while(true)
//		{
			eb.publish(dummyEvent);
			dummyEvent = eb.take();
			System.out.println(dummyEvent);
//		}
	}
	
//	private final static String event1 =
//		"<?xml version=\"1.0\" encoding=\"ASCII\"?>\n" + 
//		"<wfv:Event xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:action=\"http://alive/architecture/actions\" xmlns:actions=\"http://alive/coordinationLevel/actions\" xmlns:event=\"http://alive/architecture/event\" xmlns:fact=\"http://alive/architecture/fact\" xmlns:wfv=\"http://alive/architecture/event\"\n" + 
//		"    asserter=\"//@Actor.0\" encoding=\"XML\" timestamp=\"2010-01-29T12:56:09.454+0000\">\n" + 
//		"    <content>\n" + 
//		"      <fact xsi:type=\"fact:SendAct\" sender=\"//@Actor.0\" receiver=\"//@Actor.1\"/>\n" + 
//		"      <effect xsi:type=\"action:PlanSynthesis\" byActor=\"//@Actor.0\"/>\n" + 
//		"    </content>\n" + 
//		"    <pointOfView xsi:type=\"event:ActorView\"/>\n" + 
//		"    <language xsi:type=\"event:XMLSchema\"/>\n" + 
//		"  </event>\n" + 
//		"  <event:Actor name=\"PlanningAgent\" agent=\"//@agents.0\" emit=\"//@event.0 //@event.1 //@event.6\"/>\n" + 
//		"  <event:Actor name=\"PlanningService\" agent=\"//@agents.1\"/>\n" + 
//		"  <event:Actor name=\"CLAgents\" agent=\"//@agents.2\"/>\n" + 
//		"  <event:Actor name=\"Participant\" agent=\"//@agent.3\" emit=\"//@event.2 //@event.7 //@event.8 //@event.9 //@event.10 //@event.11\"/>\n" + 
//		"  <event:Actor name=\"Subscription_Manager\" agent=\"//@agent.4\" emit=\"//@event.3\"/>\n" + 
//		"  <event:Actor name=\"Profile_Manager\" agent=\"//@agent.5\" emit=\"//@event.4\"/>\n" + 
//		"  <event:Actor name=\"Context_Manager\" agent=\"//@agent.6\" emit=\"//@event.5\"/>\n" + 
//		"  <event:agent name=\"PlanningAgent\" Actor=\"//@Actor.0\"/>\n" + 
//		"  <event:agent name=\"PlanningService\" Actor=\"//@Actor.1\"/>\n" + 
//		"  <event:agent name=\"CLagent\" Actor=\"//@Actor.2\"/>\n" + 
//		"  <event:agent name=\"Participant\" Actor=\"//@Actor.3\"/>\n" + 
//		"  <event:agent name=\"Subscription_Manager\" Actor=\"//@Actor.4\"/>\n" + 
//		"  <event:agent name=\"Profile_Manager\" Actor=\"//@Actor.5\"/>\n" + 
//		"  <event:agent name=\"Context_Manager\" Actor=\"//@Actor.6\"/>\n" + 
//		"</wfv:Event>\n" + 
//		"";
//	
//	private static final String blah =
//		"  <plans>\n" + 
//		"    <forTask href=\"../input/cjDelftDemo/Demo.tasks#//@tasks.0\"/>\n" + 
//		"    <hasAtomicProcessGroundingList>\n" + 
//		"      <first>\n" + 
//		"        <owlsProcess xsi:type=\"actions:AtomicAction\" href=\"../input/cjDelftDemo/Demo.actions#//@actions.0\"/>\n" + 
//		"        <input messagePart=\"http://mas.owl#msg\">\n" + 
//		"          <parameter href=\"../input/cjDelftDemo/Demo.actions#//@parameters.0\"/>\n" + 
//		"        </input>\n" + 
//		"        <input messagePart=\"http://www.csd.abdn.ac.uk/~dcorsar/ALIVE/CalicoJack.owl#Email\">\n" + 
//		"          <parameter href=\"../input/cjDelftDemo/Demo.actions#//@parameters.1\"/>\n" + 
//		"        </input>\n" + 
//		"      </first>\n" + 
//		"    </hasAtomicProcessGroundingList>\n" + 
//		"  </plans>\n" + 
//		"  <events asserter=\"//@actors.0\" encoding=\"XML\" timestamp=\"2010-01-29T12:56:10.916+0000\">\n" + 
//		"    <content>\n" + 
//		"      <fact xsi:type=\"fact:SendAct\" sender=\"//@actors.0\" receiver=\"//@actors.2\"/>\n" + 
//		"    </content>\n" + 
//		"    <pointOfView xsi:type=\"event:ActorView\"/>\n" + 
//		"    <language xsi:type=\"event:XMLSchema\"/>\n" + 
//		"  </events>\n" + 
//		"  <events asserter=\"//@actors.3\" encoding=\"XML\" timestamp=\"2010-01-29T12:56:11.035+0000\">\n" + 
//		"    <content>\n" + 
//		"      <fact xsi:type=\"fact:ReceiveAct\" sender=\"//@actors.0\" receiver=\"//@actors.3\"/>\n" + 
//		"    </content>\n" + 
//		"    <pointOfView xsi:type=\"event:ActorView\"/>\n" + 
//		"    <language xsi:type=\"event:XMLSchema\"/>\n" + 
//		"  </events>\n" + 
//		"  <events asserter=\"//@actors.4\" encoding=\"XML\" timestamp=\"2010-01-29T12:56:11.093+0000\">\n" + 
//		"    <content>\n" + 
//		"      <fact xsi:type=\"fact:ReceiveAct\" sender=\"//@actors.0\" receiver=\"//@actors.4\"/>\n" + 
//		"    </content>\n" + 
//		"    <pointOfView xsi:type=\"event:ActorView\"/>\n" + 
//		"    <language xsi:type=\"event:XMLSchema\"/>\n" + 
//		"  </events>\n" + 
//		"  <events asserter=\"//@actors.5\" encoding=\"XML\" timestamp=\"2010-01-29T12:56:11.223+0000\">\n" + 
//		"    <content>\n" + 
//		"      <fact xsi:type=\"fact:ReceiveAct\" sender=\"//@actors.0\" receiver=\"//@actors.5\"/>\n" + 
//		"    </content>\n" + 
//		"    <pointOfView xsi:type=\"event:ActorView\"/>\n" + 
//		"    <language xsi:type=\"event:XMLSchema\"/>\n" + 
//		"  </events>\n" + 
//		"  <events asserter=\"//@actors.6\" encoding=\"XML\" timestamp=\"2010-01-29T12:56:11.131+0000\">\n" + 
//		"    <content>\n" + 
//		"      <fact xsi:type=\"fact:ReceiveAct\" sender=\"//@actors.0\" receiver=\"//@actors.6\"/>\n" + 
//		"    </content>\n" + 
//		"    <pointOfView xsi:type=\"event:ActorView\"/>\n" + 
//		"    <language xsi:type=\"event:XMLSchema\"/>\n" + 
//		"  </events>\n" + 
//		"  <events asserter=\"//@actors.0\" encoding=\"XML\" timestamp=\"2010-01-29T12:56:11.303+0000\">\n" + 
//		"    <content>\n" + 
//		"      <fact xsi:type=\"fact:SendAct\" sender=\"//@actors.0\" receiver=\"//@actors.2\"/>\n" + 
//		"    </content>\n" + 
//		"    <pointOfView xsi:type=\"event:ActorView\"/>\n" + 
//		"    <language xsi:type=\"event:XMLSchema\"/>\n" + 
//		"  </events>\n" + 
//		"  <events asserter=\"//@actors.3\" encoding=\"XML\" timestamp=\"2010-01-29T12:56:12.445+0000\">\n" + 
//		"    <content>\n" + 
//		"      <fact xsi:type=\"fact:StartedAct\"/>\n" + 
//		"    </content>\n" + 
//		"    <pointOfView xsi:type=\"event:ActorView\"/>\n" + 
//		"    <language xsi:type=\"event:XMLSchema\"/>\n" + 
//		"  </events>\n" + 
//		"  <events asserter=\"//@actors.3\" encoding=\"XML\" timestamp=\"2010-01-29T12:56:12.667+0000\">\n" + 
//		"    <content>\n" + 
//		"      <fact xsi:type=\"fact:SendAct\" sender=\"//@actors.3\" receiver=\"//@actors.3\">\n" + 
//		"        <sendMessage/>\n" + 
//		"      </fact>\n" + 
//		"    </content>\n" + 
//		"    <pointOfView xsi:type=\"event:ActorView\"/>\n" + 
//		"    <language xsi:type=\"event:XMLSchema\"/>\n" + 
//		"  </events>\n" + 
//		"  <events asserter=\"//@actors.3\" encoding=\"XML\" timestamp=\"2010-01-29T12:56:15.315+0000\">\n" + 
//		"    <content>\n" + 
//		"      <fact xsi:type=\"fact:StartedAct\">\n" + 
//		"        <action xsi:type=\"action:ActionEnactment\" byActor=\"//@actors.3\" plan=\"//@plans.0\"/>\n" + 
//		"      </fact>\n" + 
//		"    </content>\n" + 
//		"    <pointOfView xsi:type=\"event:ActorView\"/>\n" + 
//		"    <language xsi:type=\"event:XMLSchema\"/>\n" + 
//		"  </events>\n" + 
//		"  <events asserter=\"//@actors.3\" encoding=\"XML\" timestamp=\"2010-01-29T12:56:18.895+0000\">\n" + 
//		"    <content>\n" + 
//		"      <fact xsi:type=\"fact:SendAct\" sender=\"//@actors.3\" receiver=\"//@actors.2\"/>\n" + 
//		"    </content>\n" + 
//		"    <pointOfView xsi:type=\"event:ActorView\"/>\n" + 
//		"    <language xsi:type=\"event:XMLSchema\"/>\n" + 
//		"  </events>\n" + 
//		"  <events asserter=\"//@actors.3\" encoding=\"XML\" timestamp=\"2010-01-29T12:56:19.015+0000\">\n" + 
//		"    <content>\n" + 
//		"      <fact xsi:type=\"fact:ExecutedAct\"/>\n" + 
//		"    </content>\n" + 
//		"    <pointOfView xsi:type=\"event:ActorView\"/>\n" + 
//		"    <language xsi:type=\"event:XMLSchema\"/>\n" + 
//		"  </events>\n" + 
//		"";
}
