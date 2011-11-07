package apapl.data;

import apapl.SubstList;
import java.util.ArrayList;

/**
 * The truth, the whole truth and nothing but the truth.
 */
public class True extends Query
{
	/**
	 * Constructs the truth.
	 */
	public True()
	{
	}
	
	public String toString(boolean inplan)
	{
		return "true";
	}
	
	/**
	 * Converts this object to a format Prolog can handle.
	 * @return the Prolog format string
	 */
	public String toPrologString()
	{
		return toString();
	}
	
	/**
	 * @param q the query to campore with
	 * @return true if q is instance of <code>True</code>, false otherwise
	 */
	public boolean equals(Query q)
	{
		return (q instanceof True);
	}

	/**
	 * Applies a substitution. Does not affect this object.
	 * 
	 * @param theta the substitution to apply
	 */
	public void applySubstitution(SubstList<Term> theta)
	{
	}
	
	/**
	 * Clones the truth.
	 * 
	 * @return the clone
	 */
	public True clone()
	{
		return new True();
	}
	
	/**
	 * Returns all variables that occur inside the truth.
	 * 
	 * @return an empty list
	 */
	public ArrayList<String> getVariables()
	{
		return new ArrayList<String>();
	}
	
	/**
	 * Nothing to evaluate.
	 */
	public void evaluate()
	{
	}
	
	/**
	 * @return false
	 */
	public boolean containsNots()
	{
		return false;
	}
	
	/**
	 * @return 1
	 */
	public int size()
	{
		return 1;
	}
	
	public String toRTF(boolean inplan)
	{
		return "\\cf5 true \\cf0";
	}
	
	/**
	 * No variables to change into unique variables.
	 * @param unfresh the list of variables that cannot be used anymore
	 * @param own all the variables in the context of this term.           
	 * @param changes list [[old,new],...] of substitutions 
	 */
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
	}
	
	/**
	 * @return false
	 */
	public boolean containsDisjunct()
	{
		return false;
	}
}