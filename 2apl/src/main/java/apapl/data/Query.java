package apapl.data;

import apapl.SubstList;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This class defines a query that can be performed on a Prolog engine (typically, a
 * belief base or a goal).
 */
public abstract class Query
{
	/**
	 * Applies a substitution to this query.
	 * 
	 * @param theta the substitution to apply
	 */
	public abstract void applySubstitution(SubstList<Term> theta);
	
	/**
	 * Clones this query.
	 * 
	 * @return a clone of the query
	 */
	public abstract Query clone();
	
	/**
	 * Converts this query to a format that can be handled by Prolog
	 * 
	 * @return the Prolog format
	 */
	public abstract String toPrologString();
	
	/**
	 * Returns all variables that occur in this query.
	 * 
	 * @return the lsit of variables
	 */
	public abstract ArrayList<String> getVariables();
	
	/**
	 * Evaluates all functions (such as arithmetic expressions) that occur in this query. 
	 */
	public abstract void evaluate();
	
	/**
	 * Turn all variables of this query into fresh (unique) variables.
	 * 
	 * @param unfresh the list of variables that cannot be used anymore
	 * @param own all the variables in the context of this query. These
	 *            are the variables that occur in the rule in which the query occurs.
	 * @param changes list [[old,new],...] of substitutions 
	 */
	public abstract void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes);
	
	/**
	 * Converts this query to a string depending on whether it occurs inside a plan or not.
	 * 
	 * @param inplan indicating whether query occurs inside of a plan.
	 * @return the string representation of this query
	 * 
	 * @deprecated inplan is about the representation of this object. This should be
	 *  lifted to the view of the application.
	 */
	public abstract String toString(boolean inplan);
	public String toString() {return toString(false);}
	
	public abstract String toRTF(boolean inplan);
	public String toRTF() {return toRTF(false);}

	
	/**
	 * Returns the number of literals that occur in this query.
	 * 
	 * @return the size of this query
	 */
	public abstract int size();
	
	/**
	 * Returns whether this query contains a disjunction.
	 * 
	 * @return true if query contains a discjunction, false otherwise
	 */
	public abstract boolean containsDisjunct();
	
	/**
	 * Returns whether this query contains negations.
	 * 
	 * @return true if this query contains a negation
	 */
	public abstract boolean containsNots();
	
	/**
	 * Converts this query to a list of literals that occur in it, thereby ignoring
	 * all boolean operators.
	 * 
	 * @return the list of literals
	 */
	public LinkedList<Literal> toLiterals() {return new LinkedList<Literal>();}
	
}