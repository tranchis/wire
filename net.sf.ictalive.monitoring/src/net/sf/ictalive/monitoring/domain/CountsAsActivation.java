package net.sf.ictalive.monitoring.domain;

import java.util.Set;

public class CountsAsActivation
{
	private Formula		gamma1;
	private Set<Value>	theta;
	
	public CountsAsActivation(Formula gamma1, Set<Value> theta, Object object)
	{
		super();
		this.gamma1 = gamma1;
		this.theta = theta;
		this.object = object;
	}
	private Object		object;
	
	public void setObject(Object object)
	{
		this.object = object;
	}
	public Object getObject()
	{
		return object;
	}
	public void setGamma1(Formula gamma1)
	{
		this.gamma1 = gamma1;
	}
	public Formula getGamma1()
	{
		return gamma1;
	}
	public void setSubstitution(Set<Value> theta)
	{
		this.theta = theta;
	}
	public Set<Value> getSubstitution()
	{
		return theta;
	}
	
	
}
