package net.sf.ictalive.monitoring.domain;

import java.util.Set;

import net.sf.ictalive.monitoring.domain.Value;

public class Holds
{
	protected String		normID;
	protected Set<Value>	substitution;
	private Formula formula;
	
	public Holds(Formula formula, Set<Value> substitution)
	{
		this.setFormula(formula);
		this.substitution = substitution;
	}
	
	public Holds(String normID, Set<Value> substitution)
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

	@Override
	public String toString()
	{
		String res;

		res = this.getClass().getSimpleName() + ":" + formula + ":" + substitution;
		
		return res;
	}

	public void setFormula(Formula formula)
	{
		this.formula = formula;
	}

	public Formula getFormula()
	{
		return formula;
	}
}
