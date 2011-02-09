package net.sf.ictalive.monitoring.domain;

public class Context implements Comparable<Context>
{
	public static Context GRIP2 = new Context("GRIP2");
	public static Context GRIP3 = new Context("GRIP3");
	
	private String context;

	public Context(String context)
	{
		this.context = context;
	}
	
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}
	
	public boolean equals(Object o)
	{
		return (o instanceof Context && ((Context)o).getContext().equals(this.getContext()));
	}

	public int compareTo(Context o) {
		Context c;
		int		b;
		
		if(o instanceof Context)
		{
			c = (Context)o;
			b = this.getContext().compareTo(c.getContext());
		}
		else
		{
			throw new ClassCastException();
		}
		
		return b;
	}
}
