package net.sf.ictalive.monitoring;

import net.sf.ictalive.monitoring.errors.RuleUpdateException;
import net.sf.ictalive.monitoring.rules.Rule;

public interface IMonitor
{
	void				initialise(Rule[] listOfRules);
	void				updateRules(Rule[] listOfRules) throws RuleUpdateException;
	EventTransporter	getEventTransporter();
	RuleEngine			getRuleEngine();
}
