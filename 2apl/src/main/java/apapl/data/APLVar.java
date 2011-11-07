package apapl.data;

import apapl.SubstList;
import java.util.ArrayList;

/**
 * A variable that ranges over terms.
 */
public class APLVar extends APLListVar
{
	private String name;
	private Term subst = null;
	private boolean anonymous = false;
	
	private int freshCounter = 0;
	
	/**
	 * Constructs an anonymous variable.
	 */
	public APLVar()
	{
		name = "_";
		anonymous = true;
	}
	
	/**
	 * Constructs a variable.
	 * 
	 * @param name the name of the variable
	 */
	public APLVar(String name)
	{
		this.name = name;
	}
	
	/**
	 * Construct a variable.
	 * 
	 * @param name the name of the variable
	 * @param subst the term this variable should be substituted with
	 * @param freshCounter the unique number of the last number used in generating
	 *   fresh (unique) variables.
	 */
	public APLVar(String name, Term subst, int freshCounter)
	{
		this.name = name;
		this.subst = subst;
		this.freshCounter = freshCounter;
	}
	
	/**
	 * Returns the name of this variable.
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return name + (freshCounter==0?"":""+freshCounter);
	}
	
	/**
	 * Returns whether this variable is bound to a term.
	 * 
	 * @return true if bound, false otherwise
	 */
	public boolean isBounded()
	{
		return subst!=null;
	}
	
	/**
	 * Returns whether this variable is bound to a term given a substitution
	 * theta.
	 * 
	 * @param theta the substitution
	 * @return true if bound, false otherwise
	 */
	public boolean isBounded(SubstList<Term> theta)
	{
		if (subst!=null) return true;
		else return theta.get(getName())!=null;
	}
	
	/**
	 * Returns the term this variable is substituted with given a substitution
	 * theta.
	 * 
	 * @param theta the substitution
	 * @return the term this variable is unified with, null if unbound
	 */
	public Term getSubst(SubstList<Term> theta)
	{
		if (anonymous) return null;
		else if (subst!=null) return subst;
		else return theta.get(getName());
	}
	
	/**
	 * Returns the term this variable is substituted with.
	 * 
	 * @return the term this variable is substituted with, null if unbound
	 */
	public Term getSubst()
	{
		return subst;
	}

	
	public String toString(boolean inplan)
	{
		if (isBounded()) return (inplan?"["+getName()+"/"+subst.toString(inplan)+"]":subst.toString(inplan));
		else return getName();
	}
	
	public String toRTF(boolean inplan)
	{
		return toString(inplan);
	}
	
	/**
	 * Applies substitution theta to this variable.
	 * 
	 * @param theta the substitution to apply
	 */
	public void applySubstitution(SubstList<Term> theta)
	{
		if (anonymous) return;
		
		Term t = theta.get(getName());
		if (t!=null) subst = t;
		while (subst instanceof APLVar) {
			APLVar v = (APLVar)subst;
			if (v.isBounded()) subst = v.getSubst();
			else {
				name = v.getName();
				freshCounter = v.getFreshCounter();
				subst = theta.get(v.getName());
			}
		}
	}
	
	/**
	 * Returns the counter that keeps track of the number used in generating fresh
	 * (unique) variable names.
	 * 
	 * @return
	 */
	public int getFreshCounter()
	{
		return freshCounter;
	}
	
	/**
	 * Binds this variable to a term.
	 * 
	 * @param t the term to bind with
	 */
	public void boundTo(Term t)
	{
		subst = t;
	}
	
	/**
	 * Clones this variable.
	 * 
	 * @return the clone
	 */
	public APLVar clone()
	{
		Term substcopy = null;
		if (isBounded()) substcopy=subst.clone();
		 
		return new APLVar(new String(name),substcopy,freshCounter);
	}
	
	/**
	 * Generates a fresh (unique) name for this variable.
	 * 
	 * @param unfresh the list of variables that cannot be used anymore
	 * @param own all the variables in the context of this variable.
	 * @param changes list [[old,new],...] of substitutions 
	 */
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
		String oldName = getName();
		if (unfresh.contains(getName()))
		while (unfresh.contains(getName())||own.contains(getName())) freshCounter++;
		if (!oldName.equals(getName())){
			ArrayList<String> change = new ArrayList<String>();
			change.add(oldName);
			change.add(getName());
			changes.add(change);
		}
	}
	
	/**
	 * Returns the name of this variable
	 * 
	 * @return a list with a single element being the name of this variable.
	 */
	public ArrayList<String> getVariables()
	{
		ArrayList<String> vars = new ArrayList<String>();
		vars.add(name);
		return vars;
	}
	
	/**
	 * Return the term this variable has been substituted with.
	 * 
	 * @return the term
	 */
	public Term getTerm()
	{
		if (isBounded()) return subst; else return this;
	}
	
	/**
	 * Tests whether this variable is equal to a term.
	 * 
	 * @param other the term to compare with
	 * @return true if equal, false otherwise
	 */
	public boolean equals(Term other)
	{
		if (isBounded()) {
			return subst.equals(other);
		}
		else if (other instanceof APLVar)
		{
			APLVar term = (APLVar)other;
			if (!term.isBounded()) return name.equals(term.getName());
			else return false;
		}
		else return false;
	}
}