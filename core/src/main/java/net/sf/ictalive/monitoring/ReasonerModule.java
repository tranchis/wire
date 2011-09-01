package net.sf.ictalive.monitoring;

import net.sf.ictalive.runtime.event.Event;

public interface ReasonerModule extends IComponent
{
	void handleObservation(Event event);
}
