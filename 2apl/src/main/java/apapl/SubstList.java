package apapl;

import apapl.data.Term;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.util.Set;
import java.util.ArrayList;

/**
 * Defines a list of substitutions. A substitution is a pair (var,val) in which
 * var is the variable and val is the value by which var is substituted.
 *
 * @param <E>
 */
public class SubstList<E extends Substitutable>
{
	private HashMap<String,E> theta = new HashMap<String,E>();
	
	public SubstList()
	{
	}
	
	public void put(String var, E val)
	{
		if (var.equals("_")) return;
		theta.put(var,val);
	}
	
	public void putAll(SubstList<E> theta2)
	{
		theta.putAll(theta2.getMap());
	}
	
	public HashMap<String,E> getMap()
	{
		return theta;
	}
	
	public void remove (String s)
	{
		theta.remove(s);
	}
	
	public E get(String var)
	{
		return theta.get(var);
	}
	
	public Collection<E> values()
	{
		return theta.values();
	}
	
	public String toRTF()
	{
		String r = "[";
		for (String k : theta.keySet()) {
			E t = theta.get(k);
			r = r + k+"/"+t.toRTF()+", ";
		}
		if (r.length()>1) r = r.substring(0,r.length()-2);
		return r+"]";
	}
	
	public String toString()
	{
		String r = "[";
		for (String k : theta.keySet()) {
			E t = theta.get(k);
			r = r + k+"/"+t.toString()+", ";
		}
		if (r.length()>1) r = r.substring(0,r.length()-2);
		return r+"]";
	}
	
	public Set<String> keySet()
	{
		return theta.keySet();
	}
	
	/**
	 * Changes all the names of all the variables of the substitutions.
	 * Takes as input a list of the form [[oldVar_1,newVar_1],...] and changes
	 * every occurence of oldVar_i in the list of substitutions into newVar_i. 
	 * 
	 * @param changes
	 */
	public void applyChanges(ArrayList<ArrayList<String>> changes)
	{
		for (ArrayList<String> change : changes) {
			E subst = theta.get(change.get(0));
			if (subst!=null) {
				theta.remove(change.get(0));
				theta.put(change.get(1),subst);
			}
		}
	}
	
	/**
	 * Clones this SubstList.
	 * 
	 * @return a clone of this SubstList
	 */
	public SubstList<E> clone()
	{
		SubstList<E> clone = new SubstList<E>();
		for (String key : keySet()) clone.put(key,theta.get(key));
		return clone;
	}
			
		

}