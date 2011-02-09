package net.sf.ictalive.monitoring.domain;

import java.util.Set;

public class SubstitutedFormula
{
	private Set<Value>	substitution;
	private Formula		formula;
	
	public SubstitutedFormula(Formula formula, Set<Value> substitution)
	{
		super();
		this.substitution = substitution;
		this.formula = formula;
	}

	public Set<Value> getSubstitution()
	{
		return substitution;
	}

	public void setSubstitution(Set<Value> substitution)
	{
		this.substitution = substitution;
	}

	public Formula getFormula()
	{
		return formula;
	}

	public void setFormula(Formula formula)
	{
		this.formula = formula;
	}
}
