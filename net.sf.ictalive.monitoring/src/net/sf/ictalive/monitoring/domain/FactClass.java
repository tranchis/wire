package net.sf.ictalive.monitoring.domain;

import net.sf.ictalive.runtime.event.Event;
import net.sf.ictalive.runtime.fact.Fact;

public class FactClass
{
	private Event					event;
	private Class<? extends Fact>	factClass;
	
	public FactClass(Event event, Class<? extends Fact> factClass)
	{
		this.event = event;
		this.factClass = factClass;
	}
	
	public void setEvent(Event event)
	{
		this.event = event;
	}
	
	public Event getEvent()
	{
		return event;
	}
	
	public void setFactClass(Class<? extends Fact> factClass)
	{
		this.factClass = factClass;
	}
	
	public Class<? extends Fact> getFactClass()
	{
		return factClass;
	}
}
