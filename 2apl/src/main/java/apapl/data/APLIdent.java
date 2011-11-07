package apapl.data;

import apapl.SubstList;
import java.util.ArrayList;

/**
 * A constant symbol or a predicate of arity 0. Although a logical fact is not a term, 
 * syntactically they have the same form. That explains why in this case no distinction 
 * is made between a predicate and term.
 */
public class APLIdent extends Fact
{
	private String name;
	private boolean quoted = false;
	
	/**
	 * Constructs an APLIdent. 
	 * The identifier will be automatically quoted if it 
	 * contains other than alphanumeric characters or underscore.
	 * 
	 * @param name the identifier
	 */
	public APLIdent(String name)
	{
		this.name = name;		
		this.quoted = false;
		
		// Make the identifier quoted if it contains other than 
		// alphanumeric characters or an underscore.
		for (int i = 0; i < name.length(); i++) {      
		    final char c = name.charAt(i);
		    if ((c >= 'a') && (c <= 'z')) continue; // lowercase
		    if ((c >= 'A') && (c <= 'Z')) continue; // uppercase
		    if ((c >= '0') && (c <= '9')) continue; // numeric
		    if ( c == '_') continue;
		    
		    this.quoted = true;
		}  
	}
	
	/**
	 * Constructs an APLIdent.
	 * 
	 * @param name the identifier
	 * @param quoted indicates whether the identifier is enclosed in quotes
	 */
	public APLIdent(String name, boolean quoted)
	{
		this.name = name;
		this.quoted = quoted;
	}
	
	/**
	 * Returns the name of this identifier.
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	
	public String toString()
	{
		return quoted ? "'" + name + "'" : name ;
	}
		
	public String toString(boolean inplan)
	{
		return toString();
	}
	
	public String toRTF(boolean inplan)
	{
		return (quoted ? "\\cf6 '" + name + "'\\cf0 " : "\\cf6 " + name + "\\cf0 ");
	}
	
	/**
	 * Unifies this idenitifier with term <code>t</code> given substitution
	 * <code>theta</code>.
	 * 
	 * @param t the term
	 * @param theta the substitution
	 * @return true if can be unification was succesful, false otherwise
	 */
	public boolean groundedUnify(Term t, SubstList<Term> theta)
	{
		if (t instanceof APLVar) return groundedUnify((APLVar)t,theta);
		else if (t instanceof APLIdent) return groundedUnify((APLIdent)t,theta);
		else return false;
	}
	
	/**
	 * Returns whether the identifier is enclosed in quotes.
	 * 
	 * @return true if enclosed in quotes, false otherwise
	 */
	public boolean quoted()
	{
		return quoted;
	}
	
	/**
	 * Applies a substitution to this identifier. Does not change this identifier.
	 * 
	 * @param theta the substitution to apply
	 */
	public void applySubstitution(SubstList<Term> theta)
	{
	}
	
	/**
	 * Clones this identifier.
	 *  
	 * @return the clone
	 */
	public APLIdent clone()
	{
		return new APLIdent(new String(name), quoted);
	}
	
	/**
	 * Turn all variables of this identifier into fresh (unique) variables.
	 * 
	 * @param unfresh the list of variables that cannot be used anymore
	 * @param own all the variables in the context of this identifier. These
	 *            are the variables that occur in the rule in which the query occurs.
	 * @param changes list [[old,new],...] of substitutions 
	 */
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
	}
	
	/**
	 * Returns all variables that occur inside this identifier.
	 * 
	 * @return an empty list
	 */
	public ArrayList<String> getVariables()
	{
		return new ArrayList<String>();
	}
	
	/**
	 * Tests whether this identifier is equal to term <code>other</code>
	 * @param other
	 * @return true if equal, false otherwise
	 */
	public boolean equals(Term other)
	{
		if (other instanceof APLIdent)
		{
			APLIdent term = (APLIdent)other;
			if (!name.equals(term.getName())) return false;
			else return true;
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