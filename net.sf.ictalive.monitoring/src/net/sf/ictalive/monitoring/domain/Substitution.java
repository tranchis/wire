package net.sf.ictalive.monitoring.domain;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Substitution extends TreeSet<Value>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 502520557741102409L;

	private static Map<String,Substitution>	mapFirst;
	
	static
	{
		mapFirst = new TreeMap<String,Substitution>();
	}
	
	public Substitution()
	{
		super();
	}
	
	public static Substitution getSubstitution(Substitution s)
	{
		Substitution	res;
		String			code;
		
		code = s.getCode();
		res = mapFirst.get(code);
		if(res == null)
		{
			mapFirst.put(code, s);
			System.out.println(mapFirst);
			res = s;
		}
		
		return res;
	}

	private String getCode()
	{
		String			res;
		Iterator<Value>	it;
		Value			v;
		
		res = "";
		it = iterator();
		while(it.hasNext())
		{
			v = it.next();
			res = res + v.getKey() + ":" + v.getValue() + ":";
		}
		
		return res;
	}
}
