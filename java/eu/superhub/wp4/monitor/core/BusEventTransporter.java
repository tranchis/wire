package eu.superhub.wp4.monitor.core;

import java.io.IOException;
import java.util.Iterator;

import clojure.lang.RT;
import clojure.lang.Var;
import eu.superhub.wp4.monitor.core.domain.Instantiated;
import eu.superhub.wp4.monitor.core.domain.NormState;
import eu.superhub.wp4.monitor.core.domain.Session;
import eu.superhub.wp4.monitor.core.domain.Value;
import eu.superhub.wp4.monitor.core.domain.Violated;
import eu.superhub.wp4.monitor.core.errors.CancelSubscriptionException;
import eu.superhub.wp4.monitor.core.errors.SubscribeException;
import eu.superhub.wp4.monitor.core.rules.Fact;
import eu.superhub.wp4.monitor.eventbus.EventBus;
import eu.superhub.wp4.monitor.eventbus.exception.EventBusConnectionException;

import net.sf.ictalive.operetta.OM.OMFactory;
import net.sf.ictalive.operetta.OM.PartialStateDescription;
import net.sf.ictalive.operetta.OM.Variable;
import net.sf.ictalive.runtime.NormInstances.NormInstance;
import net.sf.ictalive.runtime.NormInstances.NormInstancesFactory;
import net.sf.ictalive.runtime.NormInstances.PartialStateDescriptionInstance;
import net.sf.ictalive.runtime.event.Event;
import net.sf.ictalive.runtime.fact.FactFactory;
import net.sf.ictalive.runtime.fact.NormInstanceActivated;
import net.sf.ictalive.runtime.fact.NormInstanceExpired;
import net.sf.ictalive.runtime.fact.NormInstanceViolated;

public class BusEventTransporter implements EventTransporter {
	private EventBus eb;
	private NormInstancesFactory nif;
	private OMFactory omf;
	private FactFactory ff;

	public BusEventTransporter() throws EventBusConnectionException {
		eb = new EventBus();
		initialise();
	}

	private void initialise() {
		eb.setActor("Monitor", "http://beholder.eye");

		nif = NormInstancesFactory.eINSTANCE;
		omf = OMFactory.eINSTANCE;
		ff = FactFactory.eINSTANCE;
	}

	public BusEventTransporter(String host, String port)
			throws EventBusConnectionException {
		eb = new EventBus(host, port);
		initialise();
	}

	public void cancelSubscription(Session session)
			throws CancelSubscriptionException {
		// TODO Auto-generated method stub

	}

	public Endpoint getEndpoint() {
		// TODO Auto-generated method stub
		return null;
	}

	public void publish(Event event) throws IOException {
		eb.publish(event);
	}

	public Session subscribe(Endpoint endpoint, Fact[] listOfFacts)
			throws SubscribeException {
		// TODO Auto-generated method stub
		return null;
	}

	public Event take() throws InterruptedException {
		return eb.take();
	}

	public void publish(NormState ns) throws Exception {
		NormInstanceActivated nia;
		NormInstanceViolated niv;
		NormInstanceExpired nie;
		NormInstance ni;
		PartialStateDescriptionInstance psdi;
		PartialStateDescription psd;
		Iterator<Value> it;
		Value itv;
		net.sf.ictalive.runtime.NormInstances.Value v;
		Variable var;
		net.sf.ictalive.runtime.fact.Fact f;

		// Load the Clojure script -- as a side effect this initializes the
		// runtime.
		RT.loadResourceScript("net/sf/ictalive/monitoring/rules/drools/ConditionParser.clj");
		// Get a reference to the foo function.
		Var foo = RT.var(
				"net.sf.ictalive.monitoring.rules.drools.ConditionParser",
				"parse-norms");

		psd = (PartialStateDescription) foo.invoke(ns.getNorm()
				.getNormActivation());

		psdi = nif.createPartialStateDescriptionInstance();
		psdi.setName(psd.getID() + "_" + ns.hashCode());
		psdi.setPartialStateDescription(psd);

		it = ns.getSubstitution().iterator();
		while (it.hasNext()) {
			itv = it.next();
			v = nif.createValue();

			var = omf.createVariable();
			var.setName(itv.getKey());

			v.setOf(var);
			v.setValue(itv.getValue() + "");

			psdi.getValue().add(v);
		}

		ni = nif.createNormInstance();
		ni.setNorm(ns.getNorm().getOriginalNorm());
		ni.setName(ns.getNorm().getNormID() + "_" + ns.hashCode());
		ni.getPartialStateDescriptionInstance().add(psdi);

		if (ns instanceof Instantiated) {
			nia = ff.createNormInstanceActivated();
			nia.setNormInstance(ni);

			f = nia;
		} else if (ns instanceof Violated) {
			niv = ff.createNormInstanceViolated();
			niv.setNormInstance(ni);

			f = niv;
		} else // if(ns instanceof Fulfilled)
		{
			nie = ff.createNormInstanceExpired();
			nie.setNormInstance(ni);

			f = nie;
		}

		eb.publish(f);
	}

	public int getSize() {
		return eb.available();
	}

	public void push(Event ev) throws IOException {
		eb.publish(ev);
	}
}
