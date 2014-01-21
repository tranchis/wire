package eu.superhub.wp4.monitor.core.rules.drools;

import java.util.Collection;

import eu.superhub.wp4.monitor.core.rules.drools.schema.Package;

public class ParsedNorms {

    private Collection<Object> norms;
    private Package rules;

    public void setNorms(Collection<Object> vector) {
	this.norms = vector;
    }

    public void setRules(Package p) {
	this.rules = p;
    }

    public Collection<Object> getNorms() {
	return norms;
    }

    public Package getRules() {
	return rules;
    }

}
