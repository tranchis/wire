package net.sf.ictalive.monitoring.domain;

public class NormBinding
{
	private Norm	norm;
	private Formula	formula;
	
	public NormBinding(Norm norm, Formula formula)
	{
		super();
		this.norm = norm;
		this.formula = formula;
	}

	public void setNorm(Norm norm)
	{
		this.norm = norm;
	}
	
	public Norm getNorm()
	{
		return norm;
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
