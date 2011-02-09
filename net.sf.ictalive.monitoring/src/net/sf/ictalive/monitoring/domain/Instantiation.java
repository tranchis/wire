package net.sf.ictalive.monitoring.domain;

import java.util.Set;

import net.sf.ictalive.monitoring.domain.Value;

public class Instantiation
{
	private String		normID;
	private Set<Value>	substitution;

	public Instantiation()
	{
		
	}
	
	public Instantiation(String normID, Set<Value> substitution)
	{
		this.normID = normID;
		this.substitution = substitution;
	}

	public String getNormID()
	{
		return normID;
	}

	public void setNormID(String normID)
	{
		this.normID = normID;
	}

	public Set<Value> getSubstitution()
	{
		return substitution;
	}

	public void setSubstitution(Set<Value> substitution)
	{
		this.substitution = substitution;
	}
}
