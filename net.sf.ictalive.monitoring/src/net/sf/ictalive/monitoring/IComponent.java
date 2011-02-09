package net.sf.ictalive.monitoring;

import java.io.IOException;

import net.sf.ictalive.monitoring.domain.Session;
import net.sf.ictalive.monitoring.errors.CancelSubscriptionException;
import net.sf.ictalive.monitoring.errors.SubscribeException;
import net.sf.ictalive.monitoring.rules.Fact;
import net.sf.ictalive.runtime.event.Event;

public interface IComponent
{
	Session		subscribe(Endpoint endpoint, Fact[] listOfFacts) throws SubscribeException;
	void		publish(Event event) throws IOException;
	void		cancelSubscription(Session session) throws CancelSubscriptionException;
	Endpoint	getEndpoint();
}
