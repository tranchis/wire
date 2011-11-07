package apapl.data;

import apapl.SubstList;

/**
 * A query composed by an AND operator.
 */
public class AndQuery extends ComposedQuery
{
	/**
	 * Constructs a new Query representing a conjuction of 2 queries.
	 * @param left query
	 * @param right query
	 */
	public AndQuery (Query left, Query right)
	{
		super(left,right);
	}
	
	/**
	 * Converts this query to a form that Prolog can handle.
	 * 
	 * @return the Prolog format string
	 */
	public String toPrologString()
	{
		return "("+left.toPrologString()+"),("+right.toPrologString()+")";
	}
	
	/**
	 * Clones this object.
	 * 
	 * @return the clone
	 */
	public AndQuery clone()
	{
		return new AndQuery(left.clone(),right.clone());
	}
	
	/**
	 * Gives a string representation of this object.
	 * 
	 * @return the string representation
	 */
	public String toString(boolean inplan)
	{
		String a = left.toString(inplan);
		String b = right.toString(inplan);
		
		if (left instanceof OrQuery) a = "("+a+")";
		if (right instanceof OrQuery) b = "("+b+")";
		
		return a + " and " + b;
		
	}
	
	public String toRTF(boolean inplan)
	{
		String a = left.toString(inplan);
		String b = right.toString(inplan);
		
		if (left instanceof OrQuery) a = "("+a+")";
		if (right instanceof OrQuery) b = "("+b+")";
		
		return a + " \\cf1 and \\cf0" + b;
	}
	
	/**
	 * Checks whether the left or right-hand side of this composed query contains 
	 * a disjunction.
	 * 
	 * @return true if this composed query contains a disjunction, false otherwise
	 */
	public boolean containsDisjunct()
	{
		return left.containsDisjunct() || right.containsDisjunct();
	}
}