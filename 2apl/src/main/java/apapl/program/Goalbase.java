package apapl.program;

import apapl.SolutionIterator;
import apapl.data.GoalCompare;
import apapl.data.Query;
import apapl.data.True;
import apapl.Logger;
import apapl.Unifier;
import apapl.APLModule;
import apapl.Parser;
import apapl.data.Goal;
import apapl.data.Literal;
import apapl.data.Term;
import apapl.Prolog;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import apapl.SubstList;
import java.util.Enumeration;
import java.util.Collections;
import java.util.Iterator;

/**
 * The base in which the goals of the module are stored. The goal base can be iterated
 * over by means of an iterator. In contrast to the belief base, goals are not stored
 * as a Prolog program, but as a collection of goals. The reason is that goals have a 
 * non-standard entailment operator.
 */
public class Goalbase extends Base implements Iterable<Goal>
{	
	private ArrayList<Goal> gb = new ArrayList<Goal>();
	
	private Logger logger = null;
	
	/**
	 * Constructs a new goal base.
	 */
	public Goalbase()
	{		
	}
	
	/**
	 * Constructs a new goal base using given list of goals
	 */
	public Goalbase(ArrayList<Goal> gb)
	{		
		this.gb = new ArrayList<Goal>(gb);
	}

	/**
	 * Performs a query on the goal base.
	 * 
	 * @param query the query to be performed
	 * @return the solutions of the query 
	 */
	public SolutionIterator doTest(Query query)
	{
		if( logger != null)
			logger.goalQuery(query.toString(),"doTest");

		for (Goal goal : this) {
			SolutionIterator solutions = goal.doTest(query);
			if (solutions.hasNext()) return solutions;
		}
		return new SolutionIterator();
	}
	
	/**
	 * Performs a query on the goal base. Returns only one of more possible
	 * solutions. 
	 * 
	 * @param query the query to be performed
	 * @return the substitution of the solution, null if the query has
	 *   no solution
	 */
	public SubstList<Term> testGoal(Query query)
	{					
		if( logger != null)
			logger.goalQuery(query.toString(),"testGoal");

		SubstList<Term> theta = null;
		if (query instanceof True) return new SubstList<Term>();
		else for (Goal goal : this) {
			ArrayList<SubstList<Term>> solutions = goal.possibleSubstitutions(query);
			if (solutions.size()>0) return solutions.get(0);
			else return null;
		}
		return null;		
	}
	
	/**
	 * Returns an iterator to iterate over the goals stored in this goal base.
	 * 
	 * @return iterator to iterate over all goals in this base
	 */
	public  Iterator<Goal> iterator()
	{
		return gb.iterator();
	}
	
	/**
	 * Asserts a goal to the goalbase. The goal is added as last of the 
	 * collection of goals.
	 * 
	 * @param goal the goal to be asserted to the goalbase
	 */
	public void assertGoal(Goal goal)
	{
		if( logger != null)
			logger.goalAddition(goal.toString(),"assertGoal");

		for (Goal g : gb) if (g.equals(goal)) return;
		goal.evaluate();
		gb.add(goal);
	}
	
	/**
	 * Asserts a goal to the goalbase. The goal is added as first of the
	 * collection of goals.
	 * 
	 * @param goal the goal to be asserted to the goalbase
	 */
	public void assertGoalHead(Goal goal)
	{
		if( logger != null)
			logger.goalAddition(goal.toString(),"asserGoalHead");

		for (Goal g : gb) if (g.equals(goal)) return;
		goal.evaluate();
		gb.add(0,goal);
	}
	
	
	/**
	 * Drops all goals X from the goal base for which X == goal.
	 * 
	 * @param goal the goal to drop.
	 */
	public void dropGoal(Goal goal)
	{

		if( logger != null)
			logger.goalRemoval(goal.toString(),"dropGoal");

		ArrayList<Goal> toRemove = new ArrayList<Goal>();
		ArrayList<Literal> toDropList = new ArrayList<Literal>();
		for (Literal l : goal) toDropList.add(l.clone());
		Collections.sort(toDropList,GoalCompare.INSTANCE);
		String toDrop = toDropList.toString();
		
		for (Goal g : gb) {
			ArrayList<Literal> goalList = new ArrayList<Literal>();
			for (Literal l : g) goalList.add(l.clone());
			Collections.sort(goalList,GoalCompare.INSTANCE);
			if (goalList.toString().equals(toDrop)) toRemove.add(g);
		}

		for (Goal g : toRemove) gb.remove(g);
	}
	
	/**
	 * Drops all goals X from the goalbase for which goal |= X.
	 * 
	 * @param goal the goal to drop.
	 */
	public void dropSubGoals(Goal goal)
	{

		if( logger != null)
			logger.goalRemoval(goal.toString(),"dropSubGoalGoals");

		ArrayList<Goal> toRemove = new ArrayList<Goal>();
		for (Goal g : gb) if (isSubGoalFor(g,goal)) toRemove.add(g);
		for (Goal g : toRemove) gb.remove(g);
	}
	
	/**
	 * Drops all goals X from the goal base for which X |= goal.
	 * 
	 * @param goal the goal to drop.
	 */
	public void dropSuperGoals(Goal goal)
	{
		if( logger != null)
			logger.goalRemoval(goal.toString(),"dropSuperGoals");

		ArrayList<Goal> toRemove = new ArrayList<Goal>();
		for (Goal g : gb) if (isSubGoalFor(goal,g)) toRemove.add(g);
		for (Goal g : toRemove) gb.remove(g);
	}
	
	/**
	 * Check wheter a is a subgoal of b or not.
	 * 
	 * @param a the possible subgoal
	 * @param b the possible supergoal
	 * @return true if a is a subgoal of b, false otherwise
	 */
	private boolean isSubGoalFor(Goal a, Goal b)
	{
		// TODO log this too
		
		ArrayList<Goal> toRemove = new ArrayList<Goal>();
		ArrayList<Literal> dropGoal = new ArrayList<Literal>();
		for (Literal l : a) dropGoal.add(l.clone());
		Collections.sort(dropGoal,GoalCompare.INSTANCE);
		
		ArrayList<Literal> goalList = new ArrayList<Literal>();
		for (Literal l : b) goalList.add(l.clone());
		Collections.sort(goalList,GoalCompare.INSTANCE);
			
		int i = 0;
		int j = 0;
		while (i<goalList.size()) {
			if (j>=dropGoal.size()) return true;
			else if (dropGoal.get(j).equals(goalList.get(i))) {i++; j++;}
			else i++;
		}
		return (j==dropGoal.size());
	}
	
	/**
	 * Converts this object to a <code>String</code> representation.
	 * 
	 * @return The <code>String</code> representation of this object.
	 */
	public String toString()
	{
		if (gb.size()>0)
			return concatWith(gb,",\n")+".\n\n";
		else return "";
	}
	
	/**
	 * Covnerts this goal base to a RTF string.
	 * 
	 * @return the RTF string
	 */
	public String toRTF()
	{
		if (gb.size()<=0) return "";
		
		String r = "";
		String s = ",\\par\n";
		for (Goal g : gb) r = r + g.toRTF(false) + s;
			
		if (r.length()>=s.length()) r = r.substring(0,r.length()-s.length());	
		return r;
	}
	
	/**
	 * Removes all goals from the goalbase that are currently achieved. A goal is
	 * achieved if it can be entailed by the belief base.
	 * 
	 * @param bb the beliefbase
	 */
	public void removeReachedGoals(Beliefbase bb)
	{
		SubstList<Term> theta = new SubstList<Term>();
		
		ArrayList<Goal> toRemove = new ArrayList<Goal>();
		for (Goal g : gb)
		if (bb.doGoalQuery(g,theta))
			toRemove.add(g);
			
		for (Goal g : toRemove)	{

			if( logger != null)
				logger.goalRemoval(g.toString(),"removeReachedGoals");

			gb.remove(g);
		}
	}

	/**
	 * @return the goalbase
	 */
	public ArrayList<Goal> getGoalbase() {
		return gb;
	}
	
	/**
	 * @return clone of the goalbase
	 */
	public Goalbase clone()
	{
		Goalbase gb = new Goalbase(this.getGoalbase());
		gb.setLogger(logger);
		return gb;
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
}
