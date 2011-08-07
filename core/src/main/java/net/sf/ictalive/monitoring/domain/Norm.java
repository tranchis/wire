package net.sf.ictalive.monitoring.domain;

import net.sf.ictalive.operetta.OM.PartialStateDescription;

public class Norm extends ConditionHolder
{
	public int									normType;
	public PartialStateDescription				normActivation, normCondition, normExpiration;
	public String								normTarget;
	private String								normID;
	private net.sf.ictalive.operetta.OM.Norm	originalNorm;
	
	public final static int OBLIGATION = 0;
	public final static int PERMISSION = 1;
	public final static int PROHIBITION = 2;
	
	public Norm(String normID, PartialStateDescription normActivation,
			PartialStateDescription normCondition,
			PartialStateDescription normExpiration, net.sf.ictalive.operetta.OM.Norm n)
	{
		this.normID = normID;
		this.normActivation = normActivation;
		this.normCondition = normCondition;
		this.normExpiration = normExpiration;
		this.setOriginalNorm(n);
	}

	public int getNormType()
	{
		return normType;
	}

	public void setNormType(int normType)
	{
		this.normType = normType;
	}

	public PartialStateDescription getNormActivation()
	{
		return normActivation;
	}

	public void setNormActivation(PartialStateDescription normActivation)
	{
		this.normActivation = normActivation;
	}

	public PartialStateDescription getNormCondition()
	{
		return normCondition;
	}

	public void setNormCondition(PartialStateDescription normCondition)
	{
		this.normCondition = normCondition;
	}

	public PartialStateDescription getNormExpiration()
	{
		return normExpiration;
	}

	public void setNormExpiration(PartialStateDescription normExpiration)
	{
		this.normExpiration = normExpiration;
	}

	public String getNormTarget()
	{
		return normTarget;
	}

	public void setNormTarget(String normTarget)
	{
		this.normTarget = normTarget;
	}

	public String getNormID()
	{
		return normID;
	}

	public void setNormID(String normID)
	{
		this.normID = normID;
	}

	@Override
	public String getID()
	{
		return normID;
	}

	@Override
	public PartialStateDescription getCondition(int mode)
	{
		PartialStateDescription	res;
		
		if(mode == ConditionHolder.ACTIVATION)
		{
			res = normActivation;
		}
		else if(mode == ConditionHolder.MAINTENANCE)
		{
			res = normCondition;
		}
		else if(mode == ConditionHolder.DEACTIVATION)
		{
			res = normExpiration;
		}
		else
		{
			throw new UnsupportedOperationException();
		}
		
		return res;
	}

	public void setOriginalNorm(net.sf.ictalive.operetta.OM.Norm originalNorm)
	{
		this.originalNorm = originalNorm;
	}

	public net.sf.ictalive.operetta.OM.Norm getOriginalNorm()
	{
		return originalNorm;
	}
}
