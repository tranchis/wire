package net.sf.ictalive.monitoring.domain;

import java.util.Set;

public class SubsetEQ
{
	private Set<Value>	subset, superset;

	public SubsetEQ(Set<Value> subset, Set<Value> superset)
	{
		this.subset = subset;
		this.superset = superset;
	}
	
	public void setSuperset(Set<Value> superset)
	{
		this.superset = superset;
	}

	public Set<Value> getSuperset()
	{
		return superset;
	}

	public void setSubset(Set<Value> subset)
	{
		this.subset = subset;
	}

	public Set<Value> getSubset()
	{
		return subset;
	}

	@Override
	public String toString()
	{
		return subset + " subset of " + superset;
	}
}
