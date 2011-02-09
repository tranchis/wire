package net.sf.ictalive.monitoring.domain;

import net.sf.ictalive.monitoring.domain.Context;

public class ClassificatoryCountsAs implements Comparable<ClassificatoryCountsAs>
{
	private Context context;
	private Class<?> c1;
	private Class<?> c2;

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public ClassificatoryCountsAs(Class<?> c1, Class<?> c2, Context context)
	{
		this.c1 = c1;
		this.c2 = c2;
		this.context = context;
	}

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

	public int compareTo(ClassificatoryCountsAs o) {
		int	b;
		ClassificatoryCountsAs cca;
		
		if(o instanceof ClassificatoryCountsAs)
		{
			cca = (ClassificatoryCountsAs)o;
			b = this.getC1().hashCode() + this.getC2().hashCode() - cca.getC1().hashCode() - cca.getC2().hashCode() + this.getContext().compareTo(cca.getContext());
		}
		else
		{
			throw new ClassCastException();
		}
		
		return b;
	}
	
}
