package net.sf.ictalive.monitoring.domain;

import net.sf.ictalive.operetta.OM.PartialStateDescription;

public abstract class ConditionHolder
{
	public static final int GAMMA1			= 1;
	public static final int CONTEXT			= 2;
	public static final int ACTIVATION		= 3;
	public static final int MAINTENANCE		= 4;
	public static final int DEACTIVATION	= 5;

	public abstract String getID();
	public abstract PartialStateDescription getCondition(int mode);
	
	public String getType(int mode)
	{
		String	res;
		
		res = null;
		switch(mode)
		{
			case GAMMA1:
				res = "gamma1";
				break;
			case CONTEXT:
				res = "context";
				break;
			case ACTIVATION:
				res = "activation";
				break;
			case MAINTENANCE:
				res = "maintenance";
				break;
			case DEACTIVATION:
				res = "deactivation";
				break;
		}
		
		if(res == null)
		{
			throw new UnsupportedOperationException();
		}
		
		return res;
	}
}
