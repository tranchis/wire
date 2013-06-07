package eu.superhub.wp4.monitor.core;

import eu.superhub.wp4.monitor.core.errors.RuleUpdateException;
import eu.superhub.wp4.monitor.core.rules.drools.schema.Package;

public interface IMonitor {
    void initialise(Package[] listOfRules);

    void updateRules(Package[] listOfRules) throws RuleUpdateException;

    EventTransporter getEventTransporter();

    RuleEngine getRuleEngine();
}
