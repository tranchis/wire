package apapl.plans;

import apapl.data.Term;
import apapl.program.Goalbase;
import apapl.program.Beliefbase;

import apapl.SubstList;
import java.util.ArrayList;

/**
 * Each plan that contains other plans (its childern) is called a parent plan.
 * The concept of parent plan is used for executing actions. When executing a
 * plan ({@link apapl.plans.Plan}, the first child plan of the parent), this
 * plan needs to replace itself with the result of executing the plan.
 */
public interface ParentPlan
{
	/**
	 * Adds the plan sequence as first plan of the parent.
	 * 
	 * @param plans the plan sequence to be added
	 */
	public void addFirst(PlanSeq plans);
	
	/**
	 * Applies a substitution to the plan.
	 * 
	 * @param theta the substitution to be applied
	 */
	public void applySubstitution(SubstList<Term> theta);
	
	/**
	 * Remove the first plan sequence of this parent.
	 */
	public void removeFirst();
	
	/**
	 * Clone this parent.
	 * 
	 * @return the clone
	 */
	public ParentPlan clone();
	
	/**
	 * Return all variables that occur inside this parent.
	 * 
	 * @return
	 */
	public ArrayList<String> getVariables();
	
	/**
	 * Tests whether the goal that activated this plan can be entailed by the belief base.
	 * 
	 * @param gb goal base
	 * @param bb belief base
	 * @return true if the goal is achieved, false otherwise
	 */
	public boolean testActivationGoal(Goalbase gb, Beliefbase bb);
}