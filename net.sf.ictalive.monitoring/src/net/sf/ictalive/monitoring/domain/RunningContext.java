package net.sf.ictalive.monitoring.domain;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.sf.ictalive.monitoring.domain.ClassificatoryCountsAs;
import net.sf.ictalive.monitoring.domain.Context;

public class RunningContext implements Comparable<RunningContext>
{
	private Set<Context> contexts;
	private TreeSet<ClassificatoryCountsAs> countsas;
	
	public TreeSet<ClassificatoryCountsAs> getCountsas() {
		return countsas;
	}

	public void setCountsas(TreeSet<ClassificatoryCountsAs> countsas)
	{
		this.countsas = countsas;		
	}

	public RunningContext(TreeSet<ClassificatoryCountsAs> countsas)
	{
		setCountsas(countsas);
	}

	public Set<Context> getContexts() {
		return contexts;
	}

	public void setContexts(Set<Context> contexts) {
		this.contexts = contexts;
	}

	public int compareTo(RunningContext o)
	{
		if(o instanceof RunningContext)
		{
			return this.hashCode() - o.hashCode();
		}
		else
		{
			throw new ClassCastException();
		}
	}

	@Override
	public String toString()
	{
		Iterator<ClassificatoryCountsAs>	it;
		String								c;
		ClassificatoryCountsAs				cca;
		
		c = new String();
		it = countsas.iterator();
		
		while(it.hasNext())
		{
			cca = (ClassificatoryCountsAs)it.next();
			c = c + cca.getContext().getContext();
		}
		
		return c;
	}
}
