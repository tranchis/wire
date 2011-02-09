package net.sf.ictalive.monitoring.domain;

import java.util.Set;

public class NormState
{
	private Norm		norm;
	private Set<Value>	substitution;
	
	public NormState(Norm norm, Set<Value> substitution)
	{
		this.norm = norm;
		this.substitution = substitution;
	}
	
	public void setNorm(Norm norm)
	{
		this.norm = norm;
	}
	public Norm getNorm()
	{
		return norm;
	}
	public void setSubstitution(Set<Value> substitution)
	{
		this.substitution = substitution;
	}
	public Set<Value> getSubstitution()
	{
		return substitution;
	}
}
