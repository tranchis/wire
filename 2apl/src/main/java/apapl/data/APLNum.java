package apapl.data;

import apapl.SubstList;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * A term corresponding to an integer or double format number.
 */
public class APLNum extends Term
{
	private BigDecimal val;
	
	/**
	 * Constructs a number.
	 * 
	 * @param val the value
	 */
	public APLNum(Number val)
	{
		this.val = new BigDecimal(val.toString());
	}

	/**
	 * Constructs a number.
	 * 
	 * @param val the value
	 */
	public APLNum(double val)
	{
		this.val = new BigDecimal(val);
	}
	
	/**
	 * Constructs a number.
	 * 
	 * @param val the value
	 */
	public APLNum(int val)
	{
		this.val = new BigDecimal(val);
	}
	
	/**
	 * Returns the value of this number.
	 * 
	 * @return the value
	 */
	public BigDecimal getVal()
	{
		return val;
	}
	
	public String toString(boolean inplan)
	{
		return val.toString();
	}
	
	public String toRTF(boolean inplan)
	{
		return toString();
	}

	/**
	 * Unifies this number with a grounded term.
	 * 
	 * @param t the term to unify with
	 * @param theta substitutions to be applied to t to make it grounded
	 * @return true if unifiable, false otherwise
	 */
	public boolean groundedUnify(Term t, SubstList<Term> theta)
	{
		if (t instanceof APLVar) return groundedUnify((APLVar)t,theta);
		else if (t instanceof APLNum) return ((APLNum)t).getVal().equals(val);
		else return false;
	}
	
	/**
	 * Helper method for groundedUnify.
	 */
	private boolean groundedUnify(APLVar t, SubstList<Term> theta)
	{
		Term subst = theta.get(t.getName());
		if (subst!=null)
			return groundedUnify(subst,theta);
		else
		{
			theta.put(t.getName(),this);
			return true;
		}
	}
	
	/**
	 * Applies a substitution to this number. Does not affect the number.
	 * 
	 * @param theta the substitution to be applied
	 */
	public void applySubstitution(SubstList<Term> theta)
	{
	}
	
	/**
	 * clones this number.
	 * 
	 * @return the clone
	 */
	public APLNum clone()
	{
		return new APLNum(val);
	}
	
	/**
	 * Turn all variables of this query into fresh (unique) variables. Since numbers
	 * do not contain variables, this method does not affect the number.
	 * 
	 * @param unfresh the list of variables that cannot be used anymore
	 * @param own all the variables in the context of this number. These
	 *            are the variables that occur in the rule in which the query occurs.
	 * @param changes list [[old,new],...] of substitutions 
	 */
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
	}
	
	/**
	 * Returns all variables that occur in this number.
	 * 
	 * @return an empty list
	 */
	public ArrayList<String> getVariables()
	{
		return new ArrayList<String>();
	}
	
	/**
	 * Converts this number to a double.
	 * 
	 * @return the double value
	 */
	public double toDouble()
	{
		return val.doubleValue();
	}
	
	/**
	 * Converts this number to an integer.
	 * 
	 * @return the integer value
	 */
	public int toInt()
	{
		return val.intValue();
	}
	
	public String toString()
	{
		return val.toString();
	}
	
	/**
	 * Test whether this number is equal to another number.
	 * 
	 * @param other the number to compare with
	 * @return true if equal, false otherwise
	 */
	public boolean equals(Term other)
	{
		if (other instanceof APLNum)
		{
			APLNum term = (APLNum)other;
			return val.equals(term.getVal());
		}
		else if (other instanceof APLVar)
		{
			APLVar term = (APLVar)other;
			if (!term.isBounded()) return false;
			else return equals(term.getSubst());
		}
		else return false;
	}

}