package eu.superhub.wp4.monitor.core.rules.drools;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.JAXBException;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp4.monitor.core.Endpoint;
import eu.superhub.wp4.monitor.core.EventTransporter;
import eu.superhub.wp4.monitor.core.RuleEngine;
import eu.superhub.wp4.monitor.core.domain.Action;
import eu.superhub.wp4.monitor.core.domain.EventTemplate;
import eu.superhub.wp4.monitor.core.domain.Proposition;
import eu.superhub.wp4.monitor.core.domain.SelfName;
import eu.superhub.wp4.monitor.core.domain.Session;
import eu.superhub.wp4.monitor.core.errors.CancelSubscriptionException;
import eu.superhub.wp4.monitor.core.errors.SubscribeException;
import eu.superhub.wp4.monitor.core.rules.Fact;
import eu.superhub.wp4.monitor.core.rules.drools.schema.Package;
import eu.superhub.wp4.monitor.core.rules.drools.schema.Rule;

public class DroolsEngine implements RuleEngine {
	private StatefulKnowledgeSession ksession;
	private XSDRuleParser xsdrp;
	private KnowledgeBase kb;
	private Logger logger;

	public DroolsEngine() {
		initialise();
	}

	public DroolsEngine(String name) {
		initialise();
		ksession.insert(new SelfName(name));
	}

	private void initialise() {
		KnowledgeBuilder kbuilder;
		Logger drLogger;

		logger = LoggerFactory.getLogger(getClass());
		drLogger = LoggerFactory.getLogger(getClass() + ".drl");
		try {
			xsdrp = new XSDRuleParser();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("Starting Monitor...");
		kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		try {
			kbuilder.add(ResourceFactory.newUrlResource(ClassLoader
					.getSystemResource("BaseRules.drl")), ResourceType.DRL);
		} catch (Exception e) {
			// kbuilder.add(ResourceFactory.newByteArrayResource(rules.getBytes()),
			// ResourceType.DRL);
			e.printStackTrace();
		}
		if (kbuilder.hasErrors()) {
			logger.error(kbuilder.getErrors().toString());
		}
		kb = KnowledgeBaseFactory.newKnowledgeBase();
		kb.addKnowledgePackages(kbuilder.getKnowledgePackages());

		ksession = kb.newStatefulKnowledgeSession();
		ksession.insert(drLogger);
	}

	public void evaluate() {
		ksession.fireAllRules();
	}

	public void cancelSubscription(Session session)
			throws CancelSubscriptionException {
		// TODO Auto-generated method stub

	}

	public Endpoint getEndpoint() {
		// TODO Auto-generated method stub
		return null;
	}

	public void publish(Event event) {
		// TODO Auto-generated method stub

	}

	public Session subscribe(Endpoint endpoint, Fact[] listOfFacts)
			throws SubscribeException {
		// TODO Auto-generated method stub
		return null;
	}

	public void handleObservation(Event event) {
		ksession.insert(event);
	}

	public void addRules(Collection<Rule> rule) {
		Package p;
		Iterator<Rule> it;

		p = new Package();
		p.setName("net.sf.ictalive.monitoring.domain");

		it = rule.iterator();
		p.getImportOrImportfunctionOrGlobal().add(it.next());
		while (it.hasNext()) {
			p.getImportOrImportfunctionOrGlobal().add(it.next());
		}

		addPackage(p);
	}

	public void handleObservation(Collection<?> objs) {
		Iterator<?> it;

		it = objs.iterator();
		while (it.hasNext()) {
			ksession.insert(it.next());
		}
	}

	public void addPackage(Package p) {
		String xml;
		KnowledgeBuilder kbuilder;

		logger.info("Adding package...");
		try {
			xml = xsdrp.serialise(p);
			logger.debug(xml);

			kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			kbuilder.add(ResourceFactory.newByteArrayResource(xml.getBytes()),
					ResourceType.XDRL);
			if (kbuilder.hasErrors()) {
				logger.error(kbuilder.getErrors().toString());
			}
			kb.addKnowledgePackages(kbuilder.getKnowledgePackages());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void dump() {
		Iterator<Object> it;
		int i;

		it = ksession.getObjects().iterator();
		i = 0;
		while (it.hasNext()) {
			logger.debug("dump[" + i + "]: " + it.next());
			i++;
		}
		logger.debug("dump[count]: " + ksession.getObjects().size());
	}

	public Set<Action> getActions() {
		QueryResults qr;
		Set<Action> sa;
		Iterator<QueryResultsRow> it;
		QueryResultsRow qrr;

		sa = new HashSet<Action>();
		qr = ksession.getQueryResults("actions");
		it = qr.iterator();

		while (it.hasNext()) {
			qrr = it.next();
			sa.add((Action) qrr.get("action"));
		}

		return sa;
	}

	public FactHandle handleObservation(Proposition predicate) {
		return ksession.insert(predicate);
	}

	public void handleObservation(EventTransporter eb) {
		ksession.insert(eb);
	}

	public void remove(FactHandle p) {
		ksession.retract(p);
	}

	public void addEventTemplate(Event ev) {
		logger.debug("Adding event template: " + ev);
		handleObservation(new EventTemplate(ev));
	}

	private void handleObservation(Object obj) {
		ksession.insert(obj);
	}
}
