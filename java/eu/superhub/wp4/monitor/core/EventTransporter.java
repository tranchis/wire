package eu.superhub.wp4.monitor.core;

import java.io.IOException;

import eu.superhub.wp4.monitor.core.domain.NormState;
import net.sf.ictalive.runtime.event.Event;

public interface EventTransporter extends IComponent {
	Event take() throws InterruptedException;

	void publish(NormState ns) throws Exception;

	void push(Event ev) throws IOException;

	int getSize();
}
