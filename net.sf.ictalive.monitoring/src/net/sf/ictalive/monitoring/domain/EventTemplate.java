package net.sf.ictalive.monitoring.domain;

import net.sf.ictalive.runtime.event.Event;

public class EventTemplate
{
	private Event event;

	public EventTemplate(Event ev)
	{
		this.setEvent(ev);
	}

	public void setEvent(Event event)
	{
		this.event = event;
	}

	public Event getEvent()
	{
		return event;
	}
}
