package net.sf.ictalive.monitoring.domain;

public class Kill extends Action
{
	private String	unit;
	private String actor;
	private String id;

	public Kill(String actor, String id, String unit)
	{
		super();
		this.unit = unit;
		this.actor = actor;
		this.id = id;
	}

	public void setUnit(String unit)
	{
		this.unit = unit;
	}

	public String getUnit()
	{
		return unit;
	}

	public String getActor()
	{
		return actor;
	}

	public void setActor(String actor)
	{
		this.actor = actor;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}
}
