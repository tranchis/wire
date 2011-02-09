package net.sf.ictalive.monitoring.domain;

import java.util.Set;

public class Violated extends NormState
{
	public Violated(Norm norm, Set<Value> substitution)
	{
		super(norm, substitution);
		// TODO Auto-generated constructor stub
	}

	private Instantiation normInstance;

	public Instantiation getInstantiation()
	{
		return normInstance;
	}

	public void setInstantiation(Instantiation normInstance)
	{
		this.normInstance = normInstance;
	}
}
