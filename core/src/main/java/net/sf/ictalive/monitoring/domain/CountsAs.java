package net.sf.ictalive.monitoring.domain;

import net.sf.ictalive.operetta.OM.PartialStateDescription;

public class CountsAs extends ConditionHolder
{
	private Formula gamma1, context;
	Formula gamma2;
	
	public Class<?> getC1() {
		return c1;
	}

	public void setC1(Class<?> c1) {
		this.c1 = c1;
	}

	public Class<?> getC2() {
		return c2;
	}

	public void setC2(Class<?> c2) {
		this.c2 = c2;
	}

	private Class<?>	c1, c2;
	private String id;
	
	public CountsAs(Class<?> c1, Class<?> c2)
	{
		this.c1 = c1;
		this.c2 = c2;
	}
	
	public CountsAs(String id, Formula gamma1, Formula gamma2, Formula context)
	{
		this.id = id;
		this.gamma1 = gamma1;
		this.gamma2 = gamma2;
		this.context = context;
	}

	public void setContext(Formula context)
	{
		this.context = context;
	}

	public Formula getContext()
	{
		return context;
	}

	public void setGamma2(Formula gamma2)
	{
		this.gamma2 = gamma2;
	}

	public Formula getGamma2()
	{
		return gamma2;
	}

	public void setGamma1(Formula gamma1)
	{
		this.gamma1 = gamma1;
	}

	public Formula getGamma1()
	{
		return gamma1;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	@Override
	public String getID()
	{
		return getId();
	}

	@Override
	public PartialStateDescription getCondition(int mode)
	{
		PartialStateDescription	res;
		
		if(mode == ConditionHolder.GAMMA1)
		{
			res = gamma1.getLogic();
		}
		else if(mode == ConditionHolder.CONTEXT)
		{
			res = context.getLogic();
		}
		else
		{
			throw new UnsupportedOperationException();
		}
		
		return res;
	}
}
