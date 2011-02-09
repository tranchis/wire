package net.sf.ictalive.monitoring.rules.drools;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import net.sf.ictalive.monitoring.domain.Activation;
import net.sf.ictalive.monitoring.domain.ConditionHolder;
import net.sf.ictalive.monitoring.domain.Deactivation;
import net.sf.ictalive.monitoring.domain.Formula;
import net.sf.ictalive.monitoring.domain.Maintenance;
import net.sf.ictalive.monitoring.rules.drools.schema.Package;
import net.sf.ictalive.monitoring.rules.drools.schema.Rule;
import net.sf.ictalive.operetta.OM.Atom;
import net.sf.ictalive.operetta.OM.Constant;
import net.sf.ictalive.operetta.OM.CountsAs;
import net.sf.ictalive.operetta.OM.Norm;
import net.sf.ictalive.operetta.OM.OMFactory;
import net.sf.ictalive.operetta.OM.PartialStateDescription;

public class NormParser {
	private ConditionParser cp;

	private Vector<net.sf.ictalive.monitoring.domain.Norm> norms;
	private Vector<net.sf.ictalive.monitoring.domain.CountsAs> countsas;

	public NormParser() {
		cp = new ConditionParser();
		norms = new Vector<net.sf.ictalive.monitoring.domain.Norm>();
		countsas = new Vector<net.sf.ictalive.monitoring.domain.CountsAs>();
	}

	public Vector<Object> getInit() {
		Vector<Object> inserts;

		Iterator<net.sf.ictalive.monitoring.domain.Norm> it;
		Iterator<net.sf.ictalive.monitoring.domain.CountsAs> itc;
		net.sf.ictalive.monitoring.domain.Norm n;
		net.sf.ictalive.monitoring.domain.CountsAs c;

		inserts = new Vector<Object>();
		it = norms.iterator();
		while (it.hasNext()) {
			n = it.next();
			inserts.add(n);
			inserts.add(new Activation(n, new Formula(n.getNormActivation())));
			inserts.add(new Maintenance(n, new Formula(n.getNormCondition())));
			inserts
					.add(new Deactivation(n, new Formula(n.getNormExpiration())));
		}

		itc = countsas.iterator();
		while (itc.hasNext()) {
			c = itc.next();
			inserts.add(c);
		}

		return inserts;
	}

	public ParsedNorms parseToPackage(String name, Collection<Norm> ns,
			Collection<CountsAs> cs) {
		Package p;
		ParsedNorms pn;
		Collection<Rule> cr;

		pn = new ParsedNorms();
		cr = parseNorms(ns);
		cr.addAll(parseCountsAs(cs));
		pn.setNorms(getInit());

		p = new Package();
		p.setName("net.sf.ictalive.monitoring.domain");

		p.getImportOrImportfunctionOrGlobal().addAll(cr);
		pn.setRules(p);

		return pn;
	}

	private Collection<Rule> parseCountsAs(Collection<CountsAs> cs) {
		Vector<Rule> vr;

		vr = new Vector<Rule>();
		for (CountsAs ca : cs) {
			vr.addAll(parseCountsAs(ca));
		}

		return vr;
	}

	public Collection<Rule> parseNorms(Collection<Norm> ns) {
		Vector<Rule> vr;

		vr = new Vector<Rule>();
		for (Norm norm : ns) {
			vr.addAll(parseNorm(norm));
		}

		return vr;
	}

	public Collection<Rule> parseNorm(Norm n) {
		Vector<Rule> vr;

		net.sf.ictalive.monitoring.domain.Norm norm;

		norm = new net.sf.ictalive.monitoring.domain.Norm(n.getNormID(), n
				.getActivationCondition(), n.getMaintenanceCondition(), n
				.getExpirationCondition(), n); // TODO:
												// n.getDeontics().getActor().getRole().getName()
												// + "");
		norms.add(norm);

		vr = new Vector<Rule>();
		vr.addAll(cp.parseCondition(norm, ConditionHolder.ACTIVATION));
		vr.addAll(cp.parseCondition(norm, ConditionHolder.MAINTENANCE));
		vr.addAll(cp.parseCondition(norm, ConditionHolder.DEACTIVATION));

		return vr;
	}

	private Collection<Rule> parseCountsAs(CountsAs ca) {
		Vector<Rule> vr;
		Atom a;
		Constant c;
		Formula f, fc;

		net.sf.ictalive.monitoring.domain.CountsAs cas;

		a = OMFactory.eINSTANCE.createAtom();
		a.setPredicate("Context");
		c = OMFactory.eINSTANCE.createConstant();
		if (ca.getContext() == null) {
			c.setName("Universal");
		} else {
			c.setName(ca.getContext().getName());
		}
		a.getArguments().add(c);
		f = new Formula((PartialStateDescription) ca.getConcreteFact());
		fc = new Formula(a);
		cas = new net.sf.ictalive.monitoring.domain.CountsAs(
				ca.hashCode() + "", f, new Formula(ca.getAbstractFact()), fc);
		countsas.add(cas);

		vr = new Vector<Rule>();
		vr.addAll(cp.parseCondition(cas, ConditionHolder.GAMMA1));
		vr.addAll(cp.parseCondition(cas, ConditionHolder.CONTEXT));

		return vr;
	}
}
