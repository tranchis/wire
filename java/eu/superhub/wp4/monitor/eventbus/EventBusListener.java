package eu.superhub.wp4.monitor.eventbus;

import net.sf.ictalive.runtime.event.Event;

public interface EventBusListener {
	public void onEvent(Event ev);

	public boolean preFilter(String xml);
}
