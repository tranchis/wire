package net.sf.ictalive.monitoring.domain;

import java.util.Collection;

public class PropositionCandidate
{
	protected String	predicate;
	protected String[]	ps;
	
	public void setParams(Collection<String> params)
	{
		this.ps = params.toArray(new String[0]);
	}
	
	public String getPredicate()
	{
		return predicate;
	}

	public void setPredicate(String predicate)
	{
		this.predicate = predicate;
	}

	public String getP0()
	{
		return getParam(0);
	}

	private String getParam(int i)
	{
		String	res;
		
		if(ps.length < i + 1)
		{
			res = null;
		}
		else
		{
			res = ps[i];
		}
		
		return res;
	}

	public void setP0(String p0)
	{
		this.ps[0] = p0;
	}

	public String getP1()
	{
		return getParam(1);
	}

	public void setP1(String p1)
	{
		this.ps[1] = p1;
	}

	public String getP2()
	{
		return getParam(2);
	}

	public void setP2(String p2)
	{
		this.ps[2] = p2;
	}

	public String getP3()
	{
		return getParam(3);
	}

	public void setP3(String p3)
	{
		this.ps[3] = p3;
	}

	public String getP4()
	{
		return getParam(4);
	}

	public void setP4(String p4)
	{
		this.ps[4] = p4;
	}

	public String getP5()
	{
		return getParam(5);
	}

	public void setP5(String p5)
	{
		this.ps[5] = p5;
	}

	public String getP6()
	{
		return getParam(6);
	}

	public void setP6(String p6)
	{
		this.ps[6] = p6;
	}

	public String getP7()
	{
		return getParam(7);
	}

	public void setP7(String p7)
	{
		this.ps[7] = p7;
	}

	public String getP8()
	{
		return getParam(8);
	}

	public void setP8(String p8)
	{
		this.ps[8] = p8;
	}

	public String getP9()
	{
		return getParam(9);
	}

	public void setP9(String p9)
	{
		this.ps[9] = p9;
	}

	public PropositionCandidate()
	{
		this.ps = new String[10];
	}

	public PropositionCandidate(String predicate, String ... ps)
	{
		int	i;
		
		this.predicate = predicate;
		this.ps = new String[10];
		for(i=0;i<ps.length;i++)
		{
			this.ps[i] = ps[i];
		}
	}
	
	public String getObject()
	{
		return predicate;
	}

	public void setObject(String predicate)
	{
		this.predicate = predicate;
	}

	@Override
	public String toString()
	{
		String res;
		int		i;
		
		res = "+" + predicate + "(";
		
		if(ps.length > 0)
		{
			res = res + ps[0];
		}
		
		for(i=1;i<ps.length;i++)
		{
			res = res + ", " + ps[i];
		}
		
		res = res + ")";
		
		return res;
	}
}
