package eu.superhub.wp4.monitor.core;

import java.io.IOException;

import eu.superhub.wp4.monitor.core.domain.Session;
import eu.superhub.wp4.monitor.core.errors.CancelSubscriptionException;
import eu.superhub.wp4.monitor.core.errors.SubscribeException;
import eu.superhub.wp4.monitor.core.rules.Fact;

import net.sf.ictalive.runtime.event.Event;

public interface IComponent {
    Session subscribe(Endpoint endpoint, Fact[] listOfFacts)
	    throws SubscribeException;

    void publish(Event event) throws IOException;

    void cancelSubscription(Session session) throws CancelSubscriptionException;

    Endpoint getEndpoint();
}
