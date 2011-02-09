package net.sf.ictalive.monitoring.domain;

public class NormInstance
{
	private Norm norm;

	public NormInstance(Norm norm)
	{
		this.norm = norm;
	}

	public Norm getNorm()
	{
		return norm;
	}

	public void setNorm(Norm norm)
	{
		this.norm = norm;
	}
}
