package net.sf.ictalive.monitoring;

import net.sf.ictalive.monitoring.domain.NormState;
import net.sf.ictalive.runtime.event.Event;

public interface EventTransporter extends IComponent
{
	Event take() throws InterruptedException;
	void publish(NormState ns) throws Exception;
}
