package net.sf.ictalive.eventbus;

import net.sf.ictalive.runtime.event.Event;

public interface EventBusListener
{
	public void onEvent(Event ev);
}
