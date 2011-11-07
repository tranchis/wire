package apapl.data;

import apapl.APLModule;
import apapl.ModuleAccessException;
import apapl.SubstList;
import apapl.SolutionIterator;
import java.util.ArrayList;

/**
 * A test is a sequence of belief and/or goal tests. Tests typically occur inside 
 * testactions or conditions of conditional plans.
 */
public abstract class Test
{
	// Tested module
	protected APLIdent moduleId;
	protected Query query;
	protected Test next = null;
	
	/**
	 * Returns the query associated with this test action.
	 * 
	 * @return the query
	 */
	public Query getQuery()
	{
		return query;
	}
	
	/**
	 * Test against belief, goal or plan base.
	 * 
	 * @param module the module that is performing the test. Relative module IDs will be resolved against this module.
	 * @return solution (if any), null otherwise
	 */
	public abstract SubstList<Term> test(APLModule module);
	
	/**
	 * Adds a test as last of the sequence.
	 * 
	 * @param test the test to add
	 */
	public void addLast(Test test)
	{
		if (next == null) next = test;
		else next.addLast(test);
	}
	
	/**
	 * Clones this test.
	 * 
	 * @return the clone
	 */
	public abstract Test clone();
	
	/**
	 * Applies a substitution to this test.
	 * 
	 * @param theta the substitution to apply.
	 */
	public void applySubstitution(SubstList<Term> theta)
	{
		query.applySubstitution(theta);
		if (next!=null) next.applySubstitution(theta);
	}
	
	public String toString()
	{
		return query + (next!=null?" & " + next.toString():"");
	}
	
	public String toRTF(boolean inplan)
	{
		return query + (next!=null?" \\b&\\b0 "+next.toString():"");
	}
	
	/**
	 * Turn all variables of this test into fresh (unique) variables.
	 * 
	 * @param unfresh the list of variables that cannot be used anymore
	 * @param own all the variables in the context of this test.
	 * @param changes list [[old,new],...] of substitutions 
	 */
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
		query.freshVars(unfresh,own,changes);
		if (next !=null) next.freshVars(unfresh,own,changes);
	}
	
	/**
	 * Returns all variables that occur inside this test.
	 * @return a list of variables
	 */
	public ArrayList<String> getVariables()
	{
		ArrayList<String> variables = query.getVariables();
		if (next!=null) variables.addAll(next.getVariables());
		return variables;
	}
	
	/**
	 * Returns the next test of the sequence.
	 * 
	 * @return the next of the sequence
	 */
	public Test getNext() 
	{
		return next;
	}

	/**
	 * Returns identifier of the module on which the test operates.
	 * 
	 * @return the module identifier
	 */
	public APLIdent getModuleId() 
	{
		return moduleId;
	}
}
	
	