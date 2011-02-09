package net.sf.ictalive.monitoring.rules.drools;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.JAXBException;

import net.sf.ictalive.monitoring.Endpoint;
import net.sf.ictalive.monitoring.EventTransporter;
import net.sf.ictalive.monitoring.RuleEngine;
import net.sf.ictalive.monitoring.domain.Action;
import net.sf.ictalive.monitoring.domain.EventTemplate;
import net.sf.ictalive.monitoring.domain.Proposition;
import net.sf.ictalive.monitoring.domain.SelfName;
import net.sf.ictalive.monitoring.domain.Session;
import net.sf.ictalive.monitoring.errors.CancelSubscriptionException;
import net.sf.ictalive.monitoring.errors.SubscribeException;
import net.sf.ictalive.monitoring.rules.Fact;
import net.sf.ictalive.monitoring.rules.drools.schema.Package;
import net.sf.ictalive.monitoring.rules.drools.schema.Rule;
import net.sf.ictalive.runtime.event.Event;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;

public class DroolsEngine implements RuleEngine
{
	private StatefulKnowledgeSession	ksession;
	private XSDRuleParser				xsdrp;
	private KnowledgeBase				kb;
	
	public DroolsEngine()
	{
		KnowledgeBuilder	kbuilder;

		try
		{
			xsdrp = new XSDRuleParser();
		}
		catch (JAXBException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Starting Monitor...");
		kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		try
		{
			kbuilder.add(ResourceFactory.newUrlResource(ClassLoader.getSystemResource("BaseRules.drl")), ResourceType.DRL);
		}
		catch(Exception e)
		{
			// kbuilder.add(ResourceFactory.newByteArrayResource(rules.getBytes()), ResourceType.DRL);
			e.printStackTrace();
		}
		if(kbuilder.hasErrors())
		{
		    System.out.println(kbuilder.getErrors());
		}
		kb = KnowledgeBaseFactory.newKnowledgeBase();
		kb.addKnowledgePackages(kbuilder.getKnowledgePackages());
		
		ksession = kb.newStatefulKnowledgeSession();
	}
	
	public DroolsEngine(String name)
	{
		KnowledgeBuilder	kbuilder;

		try
		{
			xsdrp = new XSDRuleParser();
		}
		catch (JAXBException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Starting Monitor...");
		kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		try
		{
			kbuilder.add(ResourceFactory.newUrlResource(ClassLoader.getSystemResource("BaseRules.drl")), ResourceType.DRL);
		}
		catch(Exception e)
		{
			//kbuilder.add(ResourceFactory.newByteArrayResource(rules.getBytes()), ResourceType.DRL);
			e.printStackTrace();
		}
		if(kbuilder.hasErrors())
		{
		    System.out.println(kbuilder.getErrors());
		}
		kb = KnowledgeBaseFactory.newKnowledgeBase();
		kb.addKnowledgePackages(kbuilder.getKnowledgePackages());
		
		ksession = kb.newStatefulKnowledgeSession();
		ksession.insert(new SelfName(name));
	}

	public void evaluate()
	{
		ksession.fireAllRules();
	}

	public void cancelSubscription(Session session)
			throws CancelSubscriptionException
	{
		// TODO Auto-generated method stub
		
	}

	public Endpoint getEndpoint()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void publish(Event event)
	{
		// TODO Auto-generated method stub
		
	}

	public Session subscribe(Endpoint endpoint, Fact[] listOfFacts)
			throws SubscribeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleObservation(Event event)
	{
		ksession.insert(event);
	}

	public void addRules(Collection<Rule> rule)
	{
		Package				p;
		Iterator<Rule>		it;
		
		p = new Package();
		p.setName("net.sf.ictalive.monitoring.domain");
		
		it = rule.iterator();
		p.getImportOrImportfunctionOrGlobal().add(it.next());
		while(it.hasNext())
		{
			p.getImportOrImportfunctionOrGlobal().add(it.next());
		}
		
		addPackage(p);
	}

	public void handleObservation(Collection<?> objs)
	{
		Iterator<?>	it;
		
		it = objs.iterator();
		while(it.hasNext())
		{
			ksession.insert(it.next());
		}
	}

	public void addPackage(Package p)
	{
		String				xml;
		KnowledgeBuilder	kbuilder;
		
		try
		{
			xml = xsdrp.serialise(p);
			System.out.println(xml);
			
			kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			kbuilder.add(ResourceFactory.newByteArrayResource(xml.getBytes()), ResourceType.XDRL);
			if(kbuilder.hasErrors())
			{
			    System.out.println(kbuilder.getErrors());
			}
			kb.addKnowledgePackages(kbuilder.getKnowledgePackages());
		}
		catch (JAXBException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void dump()
	{
//		Iterator<Object>	it;
//		
//		it = ksession.getObjects().iterator();
//		while(it.hasNext())
//		{
//			System.out.println(it.next());
//		}
		System.out.println(ksession.getObjects().size());
	}
	
	public Set<Action> getActions()
	{
		QueryResults				qr;
		Set<Action>					sa;
		Iterator<QueryResultsRow>	it;
		QueryResultsRow				qrr;
		
		sa = new HashSet<Action>();
		qr = ksession.getQueryResults("actions");
		it = qr.iterator();
		
		while(it.hasNext())
		{
			qrr = it.next();
			sa.add((Action)qrr.get("action"));
		}
		
		return sa;
	}

	public FactHandle handleObservation(Proposition predicate)
	{
		return ksession.insert(predicate);
	}
	
	public void handleObservation(EventTransporter eb)
	{
		ksession.insert(eb);
	}
	
	public static void main(String args[]) throws IOException, JAXBException
	{
		DroolsEngine			de;
		Opera2Drools			o2d;
		ParsedNorms				pn;
		FactHandle				p;
		
		o2d = new Opera2Drools("Warcraft3ResourceGathering.opera");
		o2d.parse();
		pn = o2d.toDrools();
		
		de = new DroolsEngine();
		de.addPackage(pn.getRules());
		System.out.println("Adding: " + pn.getNorms());
		de.handleObservation(pn.getNorms());
		de.evaluate();
		p = de.handleObservation(new Proposition("numberOfWorkers", "3"));
		de.handleObservation(new Proposition("lessThan", "3", "5"));
		de.evaluate();
		de.remove(p);
		de.evaluate();
		de.dump();
	}
	
	private void remove(FactHandle p)
	{
		ksession.retract(p);
	}

	public void addEventTemplate(Event ev)
	{
		System.out.println("Adding event template: " + ev);
		handleObservation(new EventTemplate(ev));
	}

	private void handleObservation(Object obj)
	{
		ksession.insert(obj);
	}
}
