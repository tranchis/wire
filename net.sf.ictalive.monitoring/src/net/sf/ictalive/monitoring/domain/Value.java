package net.sf.ictalive.monitoring.domain;

import java.util.Map;
import java.util.TreeMap;

public class Value implements Comparable<Value>
{
	private String									key;
	private Object									value;
	private static Map<String,Map<Object,Value>>	values;
	
	public static Value createValue(String key, Object value)
	{
		Map<Object,Value>	map;
		Value				v;
		
		if(values == null)
		{
			values = new TreeMap<String,Map<Object,Value>>();
		}
		
		map = values.get(key);
		if(map == null)
		{
			v = new Value(key, value);
			map = new TreeMap<Object,Value>();
			map.put(value, v);
			values.put(key, map);
		}
		else
		{
			v = map.get(value);
			if(v == null)
			{
				v = new Value(key, value);
				map.put(value, v);
			}
		}
		
		return v;
	}
	
	public Value()
	{
	}
	
	private Value(String key, Object value)
	{
		this.key = key;
		this.value = value;
	}
	
	public String getKey()
	{
		return key;
	}
	public void setKey(String key)
	{
		this.key = key;
	}
	public Object getValue()
	{
		return value;
	}
	public void setValue(Object value)
	{
		this.value = value;
	}

	@Override
	public int compareTo(Value o)
	{
		int		res;
		
		res = key.compareTo(o.key);
		if(res == 0)
		{
			res = value.getClass().hashCode() - o.value.getClass().hashCode();
			if(res == 0)
			{
				res = value.toString().compareTo(o.value.toString());
			}
		}
		
		return res;
	}

	@Override
	public String toString()
	{
		return key + " : [" + value + "]";
	}
}
