package apapl.data;

import apapl.SubstList;

/**
 * A query composed by an OR operator.
 */
public class OrQuery extends ComposedQuery
{
	/**
	 * Constructs an OR query.
	 * 
	 * @param left query
	 * @param right query
	 */
	public OrQuery (Query left, Query right)
	{
		super(left,right);
	}
	
	public String toString(boolean inplan)
	{
		return left.toString(inplan) + " or " + right.toString(inplan);
	}
	
	/**
	 * Converts this query to a form that Prolog can handle.
	 * 
	 * @return the Prolog format string
	 */
	public String toPrologString()
	{
		return "("+left.toPrologString()+");("+right.toPrologString()+")";
	}
	
	/**
	 * Clones this object.
	 * 
	 * @return the clone
	 */
	public OrQuery clone()
	{
		return new OrQuery(left.clone(),right.clone());
	}
	
	public String toRTF(boolean inplan)
	{
		return left.toString(inplan) + "\\cf1  or \\cf0" + right.toString(inplan);
	}
	
	/**
	 * Checks whether this query contains a disjunction.
	 * 
	 * @return true
	 */
	public boolean containsDisjunct()
	{
		return true;
	}
}