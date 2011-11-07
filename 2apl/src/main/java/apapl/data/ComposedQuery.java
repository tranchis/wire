package apapl.data;

import apapl.SubstList;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A query that is composed by some binary operator. A composed query thus consists
 * of a left query and a right query.
 */
public abstract class ComposedQuery extends Query
{
	protected Query left;
	protected Query right;
	
	/**
	 * Constructs a composed query.
	 * 
	 * @param left the left-hand side
	 * @param right the right hand-side
	 */
	public ComposedQuery (Query left, Query right)
	{
		this.left = left;
		this.right = right;
	}
	
	/**
	 * Convert this query to Prolog format.
	 * 
	 * @return the prolog format string.
	 */
	public String toPrologString()
	{
		return "("+left.toPrologString()+");("+right.toPrologString()+")";
	}
	
	public void applySubstitution(SubstList<Term> theta)
	{
		left.applySubstitution(theta);
		right.applySubstitution(theta);
	}
	
	/**
	 * Clones this composed query.
	 * 
	 * @return the clone
	 */
	public abstract ComposedQuery clone();
	
	/**
	 * Returns all variables that occur inside this query.
	 * 
	 * @return a list of variables
	 */
	public ArrayList<String> getVariables()
	{
		ArrayList<String> vars = left.getVariables();
		vars.addAll(right.getVariables());
		return vars;
	}
	
	/**
	 * Evaluates this query.
	 */
	public void evaluate()
	{
		left.evaluate();
		right.evaluate();
	}
	
	/**
	 * Turn all variables of this query into fresh (unique) variables.
	 * 
	 * @param unfresh the list of variables that cannot be used anymore
	 * @param own all the variables in the context of this query. These
	 *            are the variables that occur in the rule in which the query occurs.
	 * @param changes list [[old,new],...] of substitutions 
	 */
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
		left.freshVars(unfresh, own, changes);
		right.freshVars(unfresh, own, changes);
	}
	
	/**
	 * Converts this composed query into a list of literals. This means that the
	 * operator that composes this query is lost.
	 * 
	 * @return the list of literals
	 */
	public LinkedList<Literal> toLiterals()
	{
		LinkedList<Literal> literals = left.toLiterals();
		literals.addAll(right.toLiterals());
		return literals;
	}
	
	/**
	 * Returns the number of literals that occur in this query.
	 * 
	 * @return the size of this query
	 */
	public int size()
	{
		return left.size() + right.size();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean containsNots()
	{
		return left.containsNots() || right.containsNots();
	}
	
	public Query getLeft() {return left;}
	public Query getRight() {return right;}
}