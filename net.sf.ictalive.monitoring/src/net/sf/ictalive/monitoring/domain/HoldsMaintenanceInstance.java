package net.sf.ictalive.monitoring.domain;

public class HoldsMaintenanceInstance
{
	private Instantiation normInstance;

	public HoldsMaintenanceInstance(Instantiation normInstance)
	{
		this.normInstance = normInstance;
	}

	public Instantiation getNormInstance()
	{
		return normInstance;
	}

	public void setNormInstance(Instantiation normInstance)
	{
		this.normInstance = normInstance;
	}
}
