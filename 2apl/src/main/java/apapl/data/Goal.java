package apapl.data;

import apapl.SolutionIterator;
import apapl.UnboundedVarException;
import apapl.Prolog;
import apapl.Parser;
import apapl.program.Beliefbase;
import apapl.program.Base;
import java.util.LinkedList;
import java.util.ArrayList;
import apapl.SubstList;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Collections;
import java.util.Iterator;

/**
 * A conjunction of literals corresponding to a single goal.
 */
public class Goal implements Iterable<Literal>
{
	private LinkedList<Literal> goal;
	
	/**
	 * Constructs a new empty goal.
	 */
	public Goal()
	{
		goal = new LinkedList<Literal>();
	}
	
	/**
	 * Constructs a conjunctive goal out of a list of literals.
	 * 
	 * @param goal the list of literals
	 */
	public Goal(LinkedList<Literal> goal)
	{
		this.goal = goal;
	}
	
	/**
	 * Adds a Literal to this goal. Used for construction only.
	 * 
	 * @param l the literal to add
	 */
	public void addLiteral(Literal l)
	{
		goal.add(l);
	}
	
	/**
	 * Returns an iterator to iterate over the literals this goal is composed of.
	 * 
	 * @return Iterator that iterates over all goals in this base.
	 */
	public Iterator<Literal> iterator()
	{
		return goal.iterator();
	}
	
	/**
	 * Performs a query on this goal and returns all possible substitutions.
	 * 
	 * @param query the query to be performed
	 * @return the list of possible substitutions
	 */
	public ArrayList<SubstList<Term>> possibleSubstitutions(Query query)
	{
		ArrayList<SubstList<Term>> solutionsList = new ArrayList<SubstList<Term>>();
		Prolog prolog = new Prolog();
		for (Literal l : goal) prolog.addPredicate(l.toPrologString());
		
		SolutionIterator solutions = prolog.doTest(query);		
		for(SubstList<Term> term: solutions) {
			solutionsList.add(term);
		}
		
		return solutionsList;
	}
	
	/**
	 * Performs a test on this goal and returns all possible solutions.
	 * 
	 * @param query the query to perform on this goal
	 * @return the possible solutions
	 */
	public SolutionIterator doTest(Query query)
	{
		ArrayList<SubstList<Term>> subst = new ArrayList<SubstList<Term>>();
		Prolog prolog = new Prolog();
		for (Literal l : goal) prolog.addPredicate(l.toPrologString());
		SolutionIterator solutions = prolog.doTest(query);
		return solutions;
	}
	
	/**
	 * Converts this goal to a format Prolog can handle.
	 * 
	 * @return the Prolog format string
	 */
	public String toPrologString()
	{
		return Base.concatWith(goal," , ");
	}	
		
	/**
	 * Compares this goal with another goal.
	 * @param goal Goal to compare.
	 * @return true if goal is equal to this goal.
	 */
	public boolean equals(Goal goal)
	{
		LinkedList<Literal> ga = new LinkedList<Literal>();
		LinkedList<Literal> gb = new LinkedList<Literal>();
				
		for (Literal l : goal) ga.add(l.clone());
		for (Literal l : this) gb.add(l.clone());
		
		if (ga.size()!=gb.size()) return false;
		
		Collections.sort(ga,GoalCompare.INSTANCE);
		Collections.sort(gb,GoalCompare.INSTANCE);
		
		for (int i=0; i<ga.size(); i++)
			if (!ga.get(i).toString().equals(gb.get(i).toString())) return false;
		return true;
	}
	
	/**
	 * Removes a literal from this goal.
	 * 
	 * @param l the literal to be removed
	 */
	public void removeLiteral(Literal l)
	{
		goal.remove(l);
	}
	
	/**
	 * Applies a substitution to this goal.
	 * 
	 * @param theta the substitution to apply
	 */
	public void applySubstitution(SubstList<Term> theta)
	{
		for (Literal l : goal) l.applySubstitution(theta);
	}
	
	/**
	 * Evaluates all literals in this goal.
	 */
	public void evaluate()
	{
		for (Literal l : goal) l.evaluate();
	}
	
	/**
	 * Clones this object.
	 * 
	 * @return the clone
	 */
	public Goal clone()
	{
		Goal copy = new Goal();
		for (Literal l : goal) copy.addLiteral(l.clone());
		return copy;
	}
	
	/**
	 * Gives a string representation of this object.
	 */
	public String toString(boolean inplan)
	{
		String r = "";
		for (Literal l : goal) r = r + l.toString(inplan) + " and ";
		if (r.length()>=5) r = r.substring(0,r.length()-5);	
		return r;
	}
	
	public String toString()
	{
		return toString(false);
	}
	
	public String toRTF(boolean inplan)
	{
		if (goal.size()<=0) return "";
		
		String r = "";
		String s = "\\cf1  and \\cf0 ";
		for (Literal l : goal) r = r + l.toRTF(inplan) + s;
			
		if (r.length()>=s.length()) r = r.substring(0,r.length()-s.length());	
		
		return r;
	}
	
	/**
	 * Turn all variables of this goal into fresh (unique) variables.
	 * 
	 * @param unfresh the list of variables that cannot be used anymore
	 * @param own all the variables in the context of this goal.
	 * @param changes list [[old,new],...] of substitutions 
	 */
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
		for (Literal l : goal) l.freshVars(unfresh,own,changes);
	}
	
	/**
	 * Returns all variables that occur in this goal.
	 * 
	 * @return a list of variables
	 */
	public ArrayList<String> getVariables()
	{
		ArrayList<String> vars = new ArrayList<String>();
		for (Literal l : goal) vars.addAll(l.getVariables());
		return vars;
	}
	
	/**
	 * Returns the number of literals this goal is composed of.
	 * 
	 * @return the number of literals
	 */
	public int size()
	{
		return goal.size();
	}
	
	/**
	 * Tests whether this goal contains no literals.
	 * 
	 * @return true if empty, false otherwise
	 */
	public boolean isEmpty()
	{
		return goal.isEmpty();
	}
	
	/**
	 * Converts this goal into a query. Useful to test whether the goal
	 * can be entailed by the belief base, as only queries can be performed on the
	 * belief base.
	 * 
	 * @return the query format of this goal
	 */
	public Query convertToQuery()
	{
		if (goal.size()==0) return new True();
		else {
			Query query = null;
			for (Literal l : goal) {
				if (query == null) query = l;
				else query = new AndQuery(query,l);
			}
			return query;
		}
	}
	
	/**
	 * Returns all literals that occur in this goal as a list.
	 * 
	 * @return the list of literals
	 */
	public LinkedList<Literal> getList()
	{
		return goal;
	}
	
	/**
	 * Counts the number of unique literals this goal is composed of.
	 * 
	 * @return the number of unique literals
	 */
	public int uniqueItems()
	{
		int uniques = 0;
		Literal[] a = goal.toArray(new Literal[0]);
		for (int i=0; i<a.length; i++) {
			boolean unique = true;
			for (int j=i+1; j<a.length; j++) {
				if (a[i].equals(a[j])) unique = false;
			}
			if (unique) uniques++;
		}
		return uniques;
	}
	
	public void unvar() throws UnboundedVarException
	{
		for (Literal l : goal) l.unvar();
	}
}
